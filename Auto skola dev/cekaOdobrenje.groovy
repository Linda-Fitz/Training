import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-2197")
def parentIssue = issue.getParentObject() 
UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
def subtasks = parentIssue.getSubTaskObjects()
def posaljizahtjevField = cfManager.getCustomFieldObject("customfield_10922")
if (!subtasks){
    return
}
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

    if(issue.getCustomFieldValue(posaljizahtjevField).toString() == "DA" ){
 	 workflowTransitionUtil.setIssue(issue);
     workflowTransitionUtil.setAction(31);
     workflowTransitionUtil.validate(); 
     workflowTransitionUtil.progress();
    }