/**
 * 
 */
package sk.oxygene.stash.allpullrequests;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.GET;

import com.atlassian.stash.comment.CommentableVisitor;
import com.atlassian.stash.content.AttributeMap;
import com.atlassian.stash.property.PropertyMap;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.pull.PullRequestState;
import com.atlassian.stash.watcher.WatchableVisitor;
import com.atlassian.stash.watcher.Watcher;


/**
 * @author jwagan
 *
 */
public class PullRequestExtended implements PullRequest{
    
    private PullRequest pullRequest;
    
    private boolean mergeable;
    
    public PullRequestExtended(final PullRequest pullRequest, final boolean mergeable) {
        this.pullRequest = pullRequest;
        this.mergeable = mergeable;
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.property.PropertySupport#getProperties()
     */
    @Override
    public PropertyMap getProperties() {
        return pullRequest.getProperties();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.content.AttributeSupport#getAttributeValues(java.lang.String)
     */
    @Override
    public Set<String> getAttributeValues(String arg0) {
        return pullRequest.getAttributeValues(arg0);
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.content.AttributeSupport#getAttributes()
     */
    @Override
    public AttributeMap getAttributes() {
        return pullRequest.getAttributes();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.comment.Commentable#accept(com.atlassian.stash.comment.CommentableVisitor)
     */
    @Override
    public <T> T accept(CommentableVisitor<T> arg0) {
        return pullRequest.accept(arg0);
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.watcher.Watchable#accept(com.atlassian.stash.watcher.WatchableVisitor)
     */
    @Override
    public <T> T accept(WatchableVisitor<T> arg0) {
        return pullRequest.accept(arg0);
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.watcher.Watchable#getWatchers()
     */
    @Override
    public Set<? extends Watcher> getWatchers() {
        return pullRequest.getWatchers();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getAuthor()
     */
    @Override
    public PullRequestParticipant getAuthor() {
        return pullRequest.getAuthor();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getCreatedDate()
     */
    @Override
    public Date getCreatedDate() {
        return pullRequest.getCreatedDate();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getDescription()
     */
    @Override
    public String getDescription() {
        return pullRequest.getDescription();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getFromRef()
     */
    @Override
    public PullRequestRef getFromRef() {
        return pullRequest.getFromRef();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getId()
     */
    @Override
    public Long getId() {
        return pullRequest.getId();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getParticipants()
     */
    @Override
    public Set<PullRequestParticipant> getParticipants() {
        return pullRequest.getParticipants();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getReviewers()
     */
    @Override
    public Set<PullRequestParticipant> getReviewers() {
        return pullRequest.getReviewers();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getState()
     */
    @Override
    public PullRequestState getState() {
        return pullRequest.getState();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getTitle()
     */
    @Override
    public String getTitle() {
        return pullRequest.getTitle();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getToRef()
     */
    @Override
    public PullRequestRef getToRef() {
        return pullRequest.getToRef();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getUpdatedDate()
     */
    @Override
    public Date getUpdatedDate() {
        return pullRequest.getUpdatedDate();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#getVersion()
     */
    @Override
    public int getVersion() {
        return pullRequest.getVersion();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#isClosed()
     */
    @Override
    public boolean isClosed() {
        return pullRequest.isClosed();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#isCrossRepository()
     */
    @Override
    public boolean isCrossRepository() {
        return pullRequest.isCrossRepository();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#isLocked()
     */
    @Override
    public boolean isLocked() {
        return pullRequest.isLocked();
    }

    /* (non-Javadoc)
     * @see com.atlassian.stash.pull.PullRequest#isOpen()
     */
    @Override
    public boolean isOpen() {
        return pullRequest.isOpen();
    }

    
    public PullRequest getPullRequest() {
        return pullRequest;
    }

    
    public boolean isMergeable() {
        return mergeable;
    }
}
