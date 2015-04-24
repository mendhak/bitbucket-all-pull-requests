package sk.oxygene.stash.allpullrequests;

import com.atlassian.stash.pull.PullRequest;


public interface PullRequestExtendedFactory {

    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest);
}
