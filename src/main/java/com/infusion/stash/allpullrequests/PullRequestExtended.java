/**
 * 
 */
package com.infusion.stash.allpullrequests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.atlassian.stash.comment.CommentableVisitor;
import com.atlassian.stash.content.AttributeMap;
import com.atlassian.stash.property.PropertyMap;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeVeto;
import com.atlassian.stash.pull.PullRequestMergeability;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.pull.PullRequestState;
import com.atlassian.stash.task.TaskCount;
import com.atlassian.stash.task.TaskState;
import com.atlassian.stash.watcher.WatchableVisitor;
import com.atlassian.stash.watcher.Watcher;


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

    /* (non-Javadoc)
     * @see com.atlassian.stash.content.AttributeSupport#getAttributes()
     */
    public AttributeMap getAttributes() {
        return pullRequest.getAttributes();
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
        for(PullRequestMergeVeto veto: mergeability.getVetos()) {
            allVetoes.add(veto.getSummaryMessage());
        }
        
        for(PullRequestMergeVeto veto: customVetoes) {
            allVetoes.add(veto.getSummaryMessage());
        }
        
        return allVetoes;
    }
    
    public List<MergeBlockerIconKeeper> getVetoIcons() {
        List<MergeBlockerIconKeeper> allVetoes = new ArrayList<MergeBlockerIconKeeper>();
        for(PullRequestMergeVeto veto: mergeability.getVetos()) {
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
    
    public void addCustomMergeVeto(PullRequestMergeVeto pullRequestMergeVeto) {
        customVetoes.add(pullRequestMergeVeto);
    }
}
