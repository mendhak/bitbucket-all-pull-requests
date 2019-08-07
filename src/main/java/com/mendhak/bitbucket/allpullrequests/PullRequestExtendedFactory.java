package com.mendhak.bitbucket.allpullrequests;

import com.atlassian.bitbucket.pull.PullRequest;


/**
 * @author jwagan
 *
 */
public interface PullRequestExtendedFactory {

    public PullRequestExtended getPullRequestExtended(PullRequest pullRequest);
}
