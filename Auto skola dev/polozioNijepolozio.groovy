import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-394")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-111")
def cf = ComponentAccessor.getCustomFieldManager()
def poeni = cf.getCustomFieldObject("customfield_10825")
def poeniTeoriju = issue.getCustomFieldValue(poeni).toDouble()

UserUtil userUtil = ComponentAccessor.getUserUtil()
  
	if(poeniTeoriju == 60 || poeniTeoriju > 60) {
    
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
        workflowTransitionUtil.setIssue(issue);
        workflowTransitionUtil.setAction(41);          
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
}   

   
   
    
