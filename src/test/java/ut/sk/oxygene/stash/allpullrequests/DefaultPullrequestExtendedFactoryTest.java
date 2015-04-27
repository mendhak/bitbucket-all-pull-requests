package ut.sk.oxygene.stash.allpullrequests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sk.oxygene.stash.allpullrequests.DefaultPullRequestExtendedFactory;
import sk.oxygene.stash.allpullrequests.PullRequestExtended;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeVeto;
import com.atlassian.stash.pull.PullRequestMergeability;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.Command;
import com.atlassian.stash.scm.ScmService;
import com.atlassian.stash.scm.pull.ScmPullRequestCommandFactory;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPullrequestExtendedFactoryTest {
    
    @Mock
    private PullRequestService pullRequestService;
    @Mock
    private ScmService scmService;
    
    private DefaultPullRequestExtendedFactory factory;
    
    @Before
    public void setup() {
        factory = new DefaultPullRequestExtendedFactory(pullRequestService, scmService);
    }
    
    @Test
    public void shouldMerge() {
        //given
        PullRequest pullRequest = getPullRequestMock();
        
        PullRequestMergeability pullRequestMergeability = getPullRequestMergeability(true, new PullRequestMergeVeto[0]);
        when(pullRequestService.canMerge(anyInt(), anyLong())).thenReturn(pullRequestMergeability); 
        
        ScmPullRequestCommandFactory commandFactory = getScmPullRequestCommandFactory(true);
        when(scmService.getPullRequestCommandFactory(eq(pullRequest))).thenReturn(commandFactory);
        
        //when
        PullRequestExtended pullRequestExtended = factory.getPullRequestExtended(pullRequest);
        List<String> vetos = pullRequestExtended.getVetos();
        
        //then
        assertThat(vetos.size(), equalTo(0));
    }
    
    @Test
    public void shouldNotMergeDueToMergeConfilct() {
        //given
        PullRequest pullRequest = getPullRequestMock();
        
        PullRequestMergeability pullRequestMergeability = getPullRequestMergeability(true, new PullRequestMergeVeto[0]);
        when(pullRequestService.canMerge(anyInt(), anyLong())).thenReturn(pullRequestMergeability); 
        
        ScmPullRequestCommandFactory commandFactory = getScmPullRequestCommandFactory(false);
        when(scmService.getPullRequestCommandFactory(eq(pullRequest))).thenReturn(commandFactory);
        
        //when
        PullRequestExtended pullRequestExtended = factory.getPullRequestExtended(pullRequest);
        List<String> vetos = pullRequestExtended.getVetos();
        
        //then
        assertThat(vetos.size(), equalTo(1));
        assertThat(vetos.get(0), equalTo(getProperties("stash-all-pull-requests-extra.properties").getProperty("pullRequest.mergeConflict.summaryMessage")));
    }

    private Properties getProperties(String fileName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
    
    private PullRequest getPullRequestMock() {
        PullRequest pullRequest = mock(PullRequest.class);
        PullRequestRef pullRequestRef = mock (PullRequestRef.class);
        Repository repository = mock(Repository.class);
        
        when(pullRequestRef.getRepository()).thenReturn(repository);
        when(pullRequest.getToRef()).thenReturn(pullRequestRef);    
        when(pullRequest.getId()).thenReturn(11223344L);
        
        return pullRequest;
    }
    
    private PullRequestMergeability getPullRequestMergeability(Boolean caMarge, PullRequestMergeVeto ... pullRequestMergeVetos) {
        PullRequestMergeability pullRequestMergeability = mock(PullRequestMergeability.class);
        when(pullRequestMergeability.canMerge()).thenReturn(caMarge);
        when(pullRequestMergeability.getVetos()).thenReturn(Arrays.asList(pullRequestMergeVetos));
        
        return pullRequestMergeability;
    }
    
    private ScmPullRequestCommandFactory getScmPullRequestCommandFactory(Boolean canMerge) {
        ScmPullRequestCommandFactory commandFactory = mock (ScmPullRequestCommandFactory.class);
        Command<Boolean> command = mock(Command.class);
        
        when(command.call()).thenReturn(canMerge);
        when(commandFactory.canMerge()).thenReturn(command);
        
        return commandFactory;
    }
    
}
