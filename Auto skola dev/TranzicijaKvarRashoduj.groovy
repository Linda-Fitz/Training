import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;

def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("CE-281")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def parentIssue = (MutableIssue)issue.getParentObject() 

UserUtil userUtil = ComponentAccessor.getUserUtil()
def subtasks = issue.getSubTaskObjects()

if (parentIssue) 
{   
    def currentParentStatus = parentIssue.getStatusId()
    if (currentParentStatus == "10103") 
    {
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
        workflowTransitionUtil.setIssue(parentIssue);
        workflowTransitionUtil.setAction(41);          
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
    }        
}
