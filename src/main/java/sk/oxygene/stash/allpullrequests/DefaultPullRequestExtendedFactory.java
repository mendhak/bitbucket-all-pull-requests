/**
 * 
 */
package sk.oxygene.stash.allpullrequests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeVeto;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.pull.PullRequestTaskSearchRequest;
import com.atlassian.stash.scm.ScmService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.task.TaskCount;


/**
 * @author jwagan
 *
 */
public class DefaultPullRequestExtendedFactory implements PullRequestExtendedFactory {

    private final PullRequestService pullRequestService;
    private final ScmService scmService;
    
    public DefaultPullRequestExtendedFactory(PullRequestService pullRequestService,
            ScmService scmService) {
        this.pullRequestService = pullRequestService;
        this.scmService = scmService;
    }
    
    @Override
    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest) {
        TaskCount taskCount = getTaskCountPerPullRequest(pullRequest);
        PullRequestExtended pullRequestExtended = new PullRequestExtended(pullRequest, pullRequestService.canMerge(
                pullRequest.getToRef().getRepository().getId(), pullRequest.getId()), taskCount);

        boolean isConflicted = isConflicted(pullRequest);
        
        if (isConflicted) {
            Properties properties = getProperties("stash-all-pull-requests-extra.properties");
            CustomPullRequestMergeVeto veto = new CustomPullRequestMergeVeto(
                    properties.getProperty("pullRequest.mergeConflict.summaryMessage"),
                    properties.getProperty("pullRequest.mergeConflict.detailedMessage"));
            pullRequestExtended.addCustomMergeVeto(veto);
        }
        
        return pullRequestExtended;
    }
    
    private TaskCount getTaskCountPerPullRequest(PullRequest pullRequest) {
        PullRequestTaskSearchRequest.Builder builder = new PullRequestTaskSearchRequest.Builder(pullRequest);
        return pullRequestService.countTasks(builder.build());
    }
    
    private boolean isConflicted(PullRequest pullRequest) {
        Boolean canMerge = this.scmService.getPullRequestCommandFactory(pullRequest).canMerge().call();

        return ((canMerge != null) && (!(canMerge.booleanValue())));
    }
    
    private Properties getProperties(String fileName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            // FIXME: there should be some info to log here
            e.printStackTrace();
        }
        return properties;
    }
    
    class CustomPullRequestMergeVeto implements PullRequestMergeVeto {
        
        private final String summaryMessage;
        private final String detailedMessage;
        
        public CustomPullRequestMergeVeto(String summaryMessage, String detailedMessage) {
            this.summaryMessage = summaryMessage;
            this.detailedMessage = detailedMessage;
        }
        
        @Override
        public String getSummaryMessage() {
            return summaryMessage;
        }

        @Override
        public String getDetailedMessage() {
            return detailedMessage;
        }
        
    }

}
