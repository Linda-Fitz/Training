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
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-681")
//def parentIssue = issue.getParentObject() 
UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
def subtasks = issue.getSubTaskObjects()

if (!subtasks){
    return
}

subtasks.each {subtask ->	
 	 workflowTransitionUtil.setIssue(subtask);
     workflowTransitionUtil.setAction(21);
     workflowTransitionUtil.validate(); 
     workflowTransitionUtil.progress();
}