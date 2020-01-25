import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import org.apache.log4j.Logger
import org.apache.log4j.Level

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
def log = Logger.getLogger("com.acme.IstekaoKupon")
log.setLevel(Level.DEBUG)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "status = AKTIVAN AND cf[11239] <= startOfDay()"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query))

log.debug(issues)   
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction (11)

for (i = issues.size() - 1; i >= 0; i--) {
    if(issues[i].getIssueTypeId() != "10900") { continue }
    MutableIssue istekliKuponi = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issues[i].key)
    workflowTransitionUtil.setIssue(istekliKuponi)
    workflowTransitionUtil.validate();
    workflowTransitionUtil.progress();
} 