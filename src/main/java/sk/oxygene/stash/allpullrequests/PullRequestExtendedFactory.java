package sk.oxygene.stash.allpullrequests;

import com.atlassian.stash.pull.PullRequest;


/**
 * @author jwagan
 *
 */
public interface PullRequestExtendedFactory {

    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest);
}
