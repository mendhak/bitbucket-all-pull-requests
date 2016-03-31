/**
 * 
 */
package com.infusion.stash.allpullrequests;

import com.atlassian.bitbucket.property.PropertyMap;
import com.atlassian.bitbucket.pull.*;
import com.atlassian.bitbucket.task.TaskCount;
import com.atlassian.bitbucket.task.TaskState;
import com.atlassian.bitbucket.validation.annotation.OptionalString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author jwagan
 *
 */
public class PullRequestExtended {
    
    private final PullRequest pullRequest;
    
    private final PullRequestMergeability mergeability;
    
    private final TaskCount taskCount;
    
    private final List<PullRequestMergeVeto> customVetoes;
    
    public PullRequestExtended(final PullRequest pullRequest, final PullRequestMergeability mergeability, TaskCount taskCount) {
        this.pullRequest = pullRequest;
        this.mergeability = mergeability;
        this.taskCount = taskCount;
        this.customVetoes = new ArrayList<PullRequestMergeVeto>();
    }

    @Nullable
    @OptionalString(size = 32768)
    public String getDescription() {
        return pullRequest.getDescription();
    }

    @Nonnull
    public PropertyMap getProperties() {
        return pullRequest.getProperties();
    }

    public boolean isLocked() {
        return pullRequest.isLocked();
    }

    public boolean isClosed() {
        return pullRequest.isClosed();
    }

    public boolean isCrossRepository() {
        return pullRequest.isCrossRepository();
    }

    public int getVersion() {
        return pullRequest.getVersion();
    }

    /* (non-Javadoc)
         * @see com.atlassian.stash.pull.PullRequest#getAuthor()
         */
    public PullRequestParticipant getAuthor() {
        return pullRequest.getAuthor();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getCreatedDate()
     */
    public Date getCreatedDate() {
        return pullRequest.getCreatedDate();
    }
    
    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getFromRef()
     */
    public PullRequestRef getFromRef() {
        return pullRequest.getFromRef();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getId()
     */
    public Long getId() {
        return pullRequest.getId();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getParticipants()
     */
    public Set<PullRequestParticipant> getParticipants() {
        return pullRequest.getParticipants();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getReviewers()
     */
    public Set<PullRequestParticipant> getReviewers() {
        return pullRequest.getReviewers();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getState()
     */
    public PullRequestState getState() {
        return pullRequest.getState();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getTitle()
     */
    public String getTitle() {
        return pullRequest.getTitle();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getToRef()
     */
    public PullRequestRef getToRef() {
        return pullRequest.getToRef();
    }
    
    public Date getUpdatedDate() {
        return pullRequest.getUpdatedDate();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#isOpen()
     */
    public boolean isOpen() {
        return pullRequest.isOpen();
    }
    
    public PullRequest getPullRequest() {
        return pullRequest;
    }
    
    public boolean isMergeable() {
        return mergeability.canMerge();
    }
    
    public List<String> getVetos() {
        List<String> allVetoes = new ArrayList<String>();
        for(PullRequestMergeVeto veto: mergeability.getVetoes()) {
            allVetoes.add(veto.getSummaryMessage());
        }
        
        for(PullRequestMergeVeto veto: customVetoes) {
            allVetoes.add(veto.getSummaryMessage());
        }
        
        return allVetoes;
    }
    
    public List<MergeBlockerIconKeeper> getVetoIcons() {
        List<MergeBlockerIconKeeper> allVetoes = new ArrayList<MergeBlockerIconKeeper>();
        for(PullRequestMergeVeto veto: mergeability.getVetoes()) {
            allVetoes.add(MergeBlockerIconKeeper.getMergeBlockerIconByMessage(veto.getSummaryMessage()));
        }
        
        for(PullRequestMergeVeto veto: customVetoes) {
            allVetoes.add(MergeBlockerIconKeeper.getMergeBlockerIconByMessage(veto.getSummaryMessage()));
        }
        
        return allVetoes;
    }
    
    public long getOpenedTasksCount() {
        return taskCount.getCount(TaskState.OPEN);
    }

    public long getClosedTasksCount() {
        return taskCount.getCount(TaskState.RESOLVED);
    }

    public void addCustomMergeVeto(PullRequestMergeVeto pullRequestMergeVeto) {
        customVetoes.add(pullRequestMergeVeto);
    }
}
