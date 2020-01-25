import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat

def log = Logger.getLogger("kvar")
log.setLevel(Level.DEBUG)
def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("CE-967")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def parentIssue = (MutableIssue)issue.getParentObject() 
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()
UserUtil userUtil = ComponentAccessor.getUserUtil()
def subtasks = issue.getSubTaskObjects()	
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    def iznosField = cfManager.getCustomFieldObject("customfield_10316")
	def iznos = issue.getCustomFieldValue(iznosField).toInteger()

 	def kvarField = cfManager.getCustomFieldObject("customfield_10326")
	def kvar = issue.getCustomFieldValue(kvarField)

	def kilometrazaField = cfManager.getCustomFieldObject("customfield_10311")
	def kilometraza = issue.getCustomFieldValue(kilometrazaField).toInteger()

	def regoField = cfManager.getCustomFieldObject("customfield_10304")
	def rego = issue.getCustomFieldValue(regoField)

	def datumgField = cfManager.getCustomFieldObject("customfield_10315")
	def datumg = issue.getCustomFieldValue(datumgField)
	def nowFormatted = new SimpleDateFormat("dd-MM-yyyy").format(datumg).toString()
 def summary = "Kvar | $nowFormatted | $rego | $kilometraza km | $kvar | $iznos \u20ac"
log.debug(summary)
    def currentParentStatus = parentIssue.getStatusId()
    if (currentParentStatus == "10103") 
    { 
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
        workflowTransitionUtil.setIssue(parentIssue);  
        //workflowTransitionUtil.setAction(41);
        workflowTransitionUtil.setAction(61);
             MutableIssue kvar1 = issueFactory.getIssue()
        	log.debug(summary)
			issue.setSummary(summary)
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
    }        



