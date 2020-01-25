import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.PromenaStatusaKupona")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-4272")

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def kuponField = cfManager.getCustomFieldObject("customfield_11503")
def kuponPolje = issue.getCustomFieldValue(kuponField)
Issue kupon = issueManager.getIssueByKeyIgnoreCase(kuponPolje.key)


