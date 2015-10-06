package sk.oxygene.stash.allpullrequests;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestOrder;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.pull.PullRequestState;
import com.atlassian.bitbucket.pull.PullRequestSearchRequest;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageImpl;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AllPullRequestsServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AllPullRequestsServlet.class);

    private final ProjectService projectService;
    private final PullRequestService pullRequestService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final WebResourceAssembler webResourceAssembler;
    private final AuthenticationContext authenticationContext;

    public AllPullRequestsServlet(ProjectService projectService,
                                  PullRequestService pullRequestService,
                                  SoyTemplateRenderer soyTemplateRenderer,
                                  WebResourceAssembler webResourceAssembler,
                                  AuthenticationContext authenticationContext) {
        this.projectService = projectService;
        this.pullRequestService = pullRequestService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webResourceAssembler = webResourceAssembler;
        this.authenticationContext = authenticationContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Project project;

        String[] path = request.getPathInfo().split("/");
        if (path.length == 2 && path[1].equals("all")) {
            project = null;
        } else if (path.length == 3 && path[1].equals("project") && !path[2].isEmpty()) {
            String projectKey = path[2];
            project = projectService.getByKey(projectKey);
        }
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        PullRequestState state;
        String activeTab = request.getParameter("state");
        if (activeTab != null && activeTab.equals("merged")) {
            state = PullRequestState.MERGED;
        } else if (activeTab != null && activeTab.equals("declined")) {
            state = PullRequestState.DECLINED;
        } else {
            state = PullRequestState.OPEN;
            activeTab = "open";
        }

        PageRequest pageRequest = new PageRequestImpl(0, 100);
        Page<PullRequest> pullRequestPage = findPullRequests(project, state, pageRequest);

        Map<String, Object> context = Maps.newHashMap();
        context.put("pullRequestPage", pullRequestPage);
        context.put("activeTab", activeTab);
        context.put("currentUser", authenticationContext.getCurrentUser());

        String template;
        if (project == null) {
            webResourceAssembler.resources().requireContext("sk.oxygene.stash.stash-all-pull-requests.all");
            template = "plugin.page.allPullRequests";
        }
        else {
            webResourceAssembler.resources().requireContext("sk.oxygene.stash.stash-all-pull-requests.project");
            context.put("project", project);
            template = "plugin.page.projectPullRequests";
        }

        response.setContentType("text/html; charset=UTF-8");
        try {
            soyTemplateRenderer.render(
                    response.getWriter(),
                    "sk.oxygene.stash.stash-all-pull-requests:server-side-soy",
                    template, context);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw new ServletException(e);
            }
        }
    }

    protected Page<PullRequest> findPullRequests(Project project, PullRequestState state, PageRequest pageRequest) {
        PullRequestSearchRequest searchRequest = (new PullRequestSearchRequest.Builder()).
                state(state).order(PullRequestOrder.NEWEST).build();

        if (project == null) {
            return pullRequestService.search(searchRequest, pageRequest);
        }

        // unfortunately, we can't use any PullRequestSearchRequest filter for this :/

        List<PullRequest> values = Lists.newLinkedList();
        boolean isLastPage = false;

        int offset = 0;
        PageRequest tmpPageRequest = new PageRequestImpl(0, 10);
        while (tmpPageRequest != null && values.size() < pageRequest.getLimit() && !isLastPage) {
            Page<PullRequest> pullRequestPage = pullRequestService.search(searchRequest, tmpPageRequest);
            if (pullRequestPage.getIsLastPage()) {
                isLastPage = true;
            }
            for (PullRequest pullRequest : pullRequestPage.getValues()) {
                if (pullRequest.getToRef().getRepository().getProject().getId() == project.getId()) {
                    if (offset >= pageRequest.getStart() && values.size() < pageRequest.getLimit()) {
                        values.add(pullRequest);
                    }
                    offset += 1;
                }
            }
            tmpPageRequest = pullRequestPage.getNextPageRequest();
        }

        return new PageImpl<PullRequest>(pageRequest, values, isLastPage);
    }

}
