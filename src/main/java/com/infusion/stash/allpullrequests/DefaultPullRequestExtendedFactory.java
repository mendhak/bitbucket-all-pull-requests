/**
 * 
 */
package com.infusion.stash.allpullrequests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeVeto;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.pull.PullRequestTaskSearchRequest;
import com.atlassian.stash.scm.ScmService;
import com.atlassian.stash.task.TaskCount;
import com.infusion.stash.allpullrequests.utils.PluginLoggerFactory;


/**
 * @author jwagan
 *
 */
public class DefaultPullRequestExtendedFactory implements PullRequestExtendedFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPullRequestExtendedFactory.class);
    
    private final PullRequestService pullRequestService;
    private final ScmService scmService;
    
    private final Properties properties;
    
    public DefaultPullRequestExtendedFactory(PullRequestService pullRequestService,
            ScmService scmService) throws IOException {
        this.pullRequestService = pullRequestService;
        this.scmService = scmService;
        
        this.properties = initializeProperties("stash-all-pull-requests.properties");
    }
    
    @Override
    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest) {
        TaskCount taskCount = getTaskCountPerPullRequest(pullRequest);
        PullRequestExtended pullRequestExtended = new PullRequestExtended(pullRequest, pullRequestService.canMerge(
                pullRequest.getToRef().getRepository().getId(), pullRequest.getId()), taskCount);

        boolean isConflicted = isConflicted(pullRequest);
        
        if (isConflicted) {
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
    
    private Properties initializeProperties(String fileName) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.warn("Unable to load properties from given file: %s. Error mesage: %s", fileName, e.getMessage());
            throw e;
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
