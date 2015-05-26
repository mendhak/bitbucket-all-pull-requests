/**
 * 
 */
package com.infusion.stash.allpullrequests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeVeto;
import com.atlassian.stash.pull.PullRequestMergeability;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.pull.PullRequestState;
import com.atlassian.stash.pull.PullRequestTaskSearchRequest;
import com.atlassian.stash.scm.ScmService;
import com.atlassian.stash.task.TaskCount;
import com.infusion.stash.allpullrequests.utils.PropertiesMapper;


/**
 * @author jwagan
 *
 */
public class DefaultPullRequestExtendedFactory implements PullRequestExtendedFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPullRequestExtendedFactory.class);
    public static final String STASH_PROPERTIES_FILENAME = "stash-all-pull-requests.properties";
    
    private final PullRequestService pullRequestService;
    private final ScmService scmService;
    
    private final Properties properties;
    
    public DefaultPullRequestExtendedFactory(PullRequestService pullRequestService,
            ScmService scmService) throws IOException {
        this.pullRequestService = pullRequestService;
        this.scmService = scmService;
        
        this.properties = initializeProperties(STASH_PROPERTIES_FILENAME);
    }
    
    @Override
    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest) {
        TaskCount taskCount = getTaskCountPerPullRequest(pullRequest);
        PullRequestExtended pullRequestExtended;

        if(pullRequest.getState().equals(PullRequestState.OPEN)) {
            pullRequestExtended = new PullRequestExtended(pullRequest, pullRequestService.canMerge(
                    pullRequest.getToRef().getRepository().getId(), pullRequest.getId()), taskCount);
            
            if (isConflicted(pullRequest)) {
                CustomPullRequestMergeVeto veto = new CustomPullRequestMergeVeto(
                        properties.getProperty(PropertiesMapper.MERGE_CONFLICT_SUMMARY_MESSAGE),
                        properties.getProperty(PropertiesMapper.MERGE_CONFLICT_DETAILED_MESSAGE));
                pullRequestExtended.addCustomMergeVeto(veto);
            }
        } else {
            PullRequestMergeability mergeability = new PullRequestMergeability() {
                
                @Override
                public boolean isConflicted() {
                    return false;
                }
                
                @Override
                public Collection<PullRequestMergeVeto> getVetos() {
                    return new ArrayList<PullRequestMergeVeto>();
                }
                
                @Override
                public boolean canMerge() {
                    return false;
                }
            };
            
            pullRequestExtended = new PullRequestExtended(pullRequest, mergeability, taskCount);
            String message;
            if(pullRequest.getState().equals(PullRequestState.MERGED)) {
                message = properties.getProperty("pullRequest.mergeability.alreadymerged");
            //DECLINED
            } else {
                message = properties.getProperty("pullRequest.mergeability.declined");
            }
            
            pullRequestExtended.addCustomMergeVeto(new CustomPullRequestMergeVeto(message, message));
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
