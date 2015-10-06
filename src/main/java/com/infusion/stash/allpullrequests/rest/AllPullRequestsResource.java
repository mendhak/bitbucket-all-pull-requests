package com.infusion.stash.allpullrequests.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.pull.PullRequestDirection;
import com.atlassian.bitbucket.pull.PullRequestSearchRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.pull.PullRequestState;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.rest.RestResource;
import com.atlassian.bitbucket.rest.util.ResponseFactory;
import com.atlassian.bitbucket.rest.util.RestUtils;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Maps;
import com.sun.jersey.spi.resource.Singleton;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({RestUtils.APPLICATION_JSON_UTF8})
@Singleton
@AnonymousAllowed
public class AllPullRequestsResource extends RestResource {

    private final PullRequestService pullRequestService;
    private final RepositoryService repositoryService;
    private final PermissionValidationService permissionValidationService;

    public AllPullRequestsResource(PullRequestService pullRequestService, RepositoryService repositoryService, PermissionValidationService permissionValidationService, I18nService i18nService) {
        super(i18nService);
        this.pullRequestService = pullRequestService;
        this.repositoryService = repositoryService;
        this.permissionValidationService = permissionValidationService;
     }

    @GET
    @Path("count")
    public Response getPullRequestCount(@QueryParam("project") String projectKey) {
        permissionValidationService.validateAuthenticated();

        PageRequest pageRequest = new PageRequestImpl(0, 10);
        long count = 0;
        while (pageRequest != null) {
            Page<? extends Repository> page = repositoryService.findByProjectKey(projectKey, pageRequest);
            for (Repository repository : page.getValues()) {
                count += pullRequestService.count(
                        new PullRequestSearchRequest.Builder()
                                .state(PullRequestState.OPEN)
                                .repositoryAndBranch(PullRequestDirection.INCOMING, repository.getId(), null)
                                .build());
            }
            if (page.getIsLastPage()) {
                break;
            }
            pageRequest = page.getNextPageRequest();
        }

        Map<String, Long> response = Maps.newHashMap();
        response.put("count", count);

        return ResponseFactory.ok(response).build();
    }

}
