import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZakaziCasVoznje")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()


workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def prakticnaObukaIssue
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)){
  	if (link.getLinkTypeId() == 10504 && link.getDestinationObject().getIssueTypeId() == "10304" && link.getDestinationObject().getStatusId() != "10301") {
  		prakticnaObukaIssue = link.getDestinationObject()
  		break
  	}
}

if (!prakticnaObukaIssue) { return }

def pocetakCasaField = cfManager.getCustomFieldObject("customfield_10919")
def krajCasaField = cfManager.getCustomFieldObject("customfield_10920")
def trajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def userZaposleniField = cfManager.getCustomFieldObject("customfield_10207")
def instruktorField = cfManager.getCustomFieldObject("customfield_10913")

def pocetakCasa = issue.getCustomFieldValue(pocetakCasaField)
def krajCasa = issue.getCustomFieldValue(krajCasaField)
def trajanje = issue.getCustomFieldValue(trajanjeField)
def instruktor = issue.getCustomFieldValue(instruktorField)
def user1 = instruktor.getCustomFieldValue(userZaposleniField)

prakticnaObukaIssue.setCustomFieldValue(pocetakCasaField, pocetakCasa)
prakticnaObukaIssue.setCustomFieldValue(krajCasaField, krajCasa)
prakticnaObukaIssue.setCustomFieldValue(trajanjeField, trajanje)
prakticnaObukaIssue.setAssignee(user1)
prakticnaObukaIssue.setReporter(currentUser)


if (prakticnaObukaIssue.getStatusId() == "10300") {
	workflowTransitionUtil.setAction (31) 
	workflowTransitionUtil.setIssue(prakticnaObukaIssue)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()				
}