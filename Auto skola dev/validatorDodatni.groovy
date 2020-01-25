import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;
import com.opensymphony.workflow.InvalidInputException

def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-2837")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def parentIssue = (MutableIssue)issue.getParentObject() 
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
def subtasks = issue.getSubTaskObjects()
def zahtjevField = cfManager.getCustomFieldObject("customfield_10922")
def zahtjev = issue.getCustomFieldValue(zahtjevField).toString()
if (parentIssue) 
{   
    def currentParentStatus = parentIssue.getStatusId()
    if (currentParentStatus == "10313" && zahtjev != "DA") 
    {   
        invalidInputException = new InvalidInputException(" Nije moguce kreirati dodatni cas na kandidatu koji je u statusu zavrseno. Ukoliko je potrebno kreirati dodatne casove, posaljite zahtjev za iste. ") 
    }        
}



