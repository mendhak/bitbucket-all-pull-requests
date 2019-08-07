package ut.com.mendhak.bitbucket.allpullrequests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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

import com.atlassian.bitbucket.pull.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.scm.Command;
import com.atlassian.bitbucket.scm.ScmService;
import com.atlassian.bitbucket.scm.pull.ScmPullRequestCommandFactory;
import com.mendhak.bitbucket.allpullrequests.DefaultPullRequestExtendedFactory;
import com.mendhak.bitbucket.allpullrequests.MergeBlockerIconKeeper;
import com.mendhak.bitbucket.allpullrequests.PullRequestExtended;
import com.mendhak.bitbucket.allpullrequests.utils.PluginLoggerFactory;

/**
 * @author jwagan
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultPullrequestExtendedFactoryTest {

    private static Properties properties;
    
    @Mock
    private PullRequestService pullRequestService;
    
    @Mock
    private ScmService scmService;
    
    @Mock
    private PluginLoggerFactory pluginLoggerFactory;
    
    private DefaultPullRequestExtendedFactory factory;
    
    @BeforeClass
    public static void initializeProperties() {
        final InputStream inputStream = DefaultPullrequestExtendedFactoryTest.class.getClassLoader().getResourceAsStream("bitbucket-all-pull-requests.properties");
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Before
    public void setUp() throws IOException {
        when(pluginLoggerFactory.getLoggerForThis(any())).thenReturn(mock(Logger.class));
        factory = new DefaultPullRequestExtendedFactory(pullRequestService, scmService);
    }
    
    @Test
    public void shouldMerge() {
        //given
        PullRequest pullRequest = getPullRequestMock();
        when(pullRequest.getState()).thenReturn(PullRequestState.OPEN);
        
        PullRequestMergeability pullRequestMergeability = getPullRequestMergeability(true, new PullRequestMergeVeto[0]);
        when(pullRequestService.canMerge(anyInt(), anyLong())).thenReturn(pullRequestMergeability); 
        
        ScmPullRequestCommandFactory commandFactory = getScmPullRequestCommandFactory(true, pullRequest);
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
        when(pullRequest.getState()).thenReturn(PullRequestState.OPEN);
        
        PullRequestMergeability pullRequestMergeability = getPullRequestMergeability(true, new PullRequestMergeVeto[0]);
        when(pullRequestService.canMerge(anyInt(), anyLong())).thenReturn(pullRequestMergeability); 
        
        ScmPullRequestCommandFactory commandFactory = getScmPullRequestCommandFactory(false, pullRequest);
        when(scmService.getPullRequestCommandFactory(eq(pullRequest))).thenReturn(commandFactory);
        
        //when
        PullRequestExtended pullRequestExtended = factory.getPullRequestExtended(pullRequest);
        List<String> vetos = pullRequestExtended.getVetos();
        
        //then
        assertThat(vetos.size(), equalTo(1));
        assertThat(vetos.get(0), equalTo(properties.getProperty("pullRequest.mergeConflict.summaryMessage")));
    }
    
    @Test
    public void shouldReturnMergedPullRequest() {
        //given
        PullRequest pullRequest = getPullRequestMock();
        when(pullRequest.getState()).thenReturn(PullRequestState.MERGED);
        
        //when
        PullRequestExtended pullRequestExtended = factory.getPullRequestExtended(pullRequest);
        
        //then
        List<MergeBlockerIconKeeper> vetoes = pullRequestExtended.getVetoIcons();
        assertThat(MergeBlockerIconKeeper.CROSS.getIconFileName(), is(vetoes.get(0).getIconFileName()));      
    }
    
    @Test
    public void shouldReturnDeclinedPullRequest() {
        //given
        PullRequest pullRequest = getPullRequestMock();
        when(pullRequest.getState()).thenReturn(PullRequestState.DECLINED);
        
        //when
        PullRequestExtended pullRequestExtended = factory.getPullRequestExtended(pullRequest);
        
        //then
        List<MergeBlockerIconKeeper> vetoes = pullRequestExtended.getVetoIcons();
        assertThat(MergeBlockerIconKeeper.CROSS.getIconFileName(), is(vetoes.get(0).getIconFileName())); 
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
    
    private PullRequestMergeability getPullRequestMergeability(final Boolean caMarge, final PullRequestMergeVeto ... pullRequestMergeVetos) {
        PullRequestMergeability pullRequestMergeability = mock(PullRequestMergeability.class);
        when(pullRequestMergeability.canMerge()).thenReturn(caMarge);
        when(pullRequestMergeability.getVetoes()).thenReturn(Arrays.asList(pullRequestMergeVetos));
        
        return pullRequestMergeability;
    }
    
    private ScmPullRequestCommandFactory getScmPullRequestCommandFactory(final Boolean canMerge, final PullRequest pullRequest) {
        ScmPullRequestCommandFactory commandFactory = mock (ScmPullRequestCommandFactory.class);
        //Command<Boolean> command = mock(Command.class);
        Command<PullRequestMergeResult> mergeResultCommand = mock(Command.class);
        PullRequestMergeResult result = mock(PullRequestMergeResult.class);

        when(commandFactory.tryMerge(pullRequest)).thenReturn(mergeResultCommand);
        when(mergeResultCommand.call()).thenReturn(result);
        when(result.getOutcome()).thenReturn(canMerge ? PullRequestMergeOutcome.CLEAN : PullRequestMergeOutcome.CONFLICTED);

        return commandFactory;
    }
    
}
