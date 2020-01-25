import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3821")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def parentIssue = (MutableIssue)issue.getParentObject() 
def datumIVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def datumIVrijemeTeorijeField = cfManager.getCustomFieldObject("customfield_10632")
def datumIVrijemeTeorije = issue.getCustomFieldValue(datumIVrijemeField)
datumIVrijemeTeorijeField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(datumIVrijemeTeorijeField), datumIVrijemeTeorije), changeHolder)
UserUtil userUtil = ComponentAccessor.getUserUtil()
def subtasks = issue.getSubTaskObjects()

if (parentIssue) 
{   
    def currentParentStatus = parentIssue.getStatusId()
    if (currentParentStatus == "10303") 
    {
   
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
		workflowTransitionUtil.setUserkey("visol")
        workflowTransitionUtil.setAction(51);
        workflowTransitionUtil.setIssue(parentIssue);          
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
    }        
}
