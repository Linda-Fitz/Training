import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.Iteration")
log.setLevel(Level.DEBUG)
def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3136")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def parentIssue = (MutableIssue)issue.getParentObject() 
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
def brojField = cfManager.getCustomFieldObject("customfield_10912")
def broj = parentIssue.getCustomFieldValue(brojField).toInteger()
def subtasks = parentIssue.getSubTaskObjects()
//int totalSubTasks = parentIssue.getSubTaskObjects().size()
//def subt = subtasks.getStatusId()
//return broj
//return totalSubTasks
def zahtjevField = cfManager.getCustomFieldObject("customfield_10922")
def zahtjev = issue.getCustomFieldValue(zahtjevField).toString()

int size = 0
subtasks.each{it->
    if (it.getStatusId() == "10501" || it.getStatusId() == "10500" || it.getStatusId() == "10313")  {
        log.debug(it.getStatusId() == "10501" )
      size += 1
    }
        }

  log.debug(size)
if (size >= broj && zahtjev != "DA" ){  
   log.debug(size)
     invalidInputException = new InvalidInputException(" Nije moguce kreirati dodatni cas jer je vec odrzan broj casova koji je ugovoren!") 
}
