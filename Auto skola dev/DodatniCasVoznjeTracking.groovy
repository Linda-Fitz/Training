import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajCasVoznje")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def projectManager = ComponentAccessor.getProjectManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

def kandidatIssue = issue.getParentObject()
def vozilo = kandidatIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324")) // Vozilo issue field

def vrstaPlacanjaField = cfManager.getCustomFieldObject("customfield_10320")
def pocetakCasaField = cfManager.getCustomFieldObject("customfield_10919")
def krajCasaField = cfManager.getCustomFieldObject("customfield_10920")
def trajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def pocetakCasa = issue.getCustomFieldValue(pocetakCasaField)
def krajCasa = issue.getCustomFieldValue(krajCasaField)
def trajanje = issue.getCustomFieldValue(trajanjeField)



// TRACCAR URL
def baseUrl = "https://asteam.ddns.net:3000/Traccar.html?"
def deviceId = vozilo?.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11225"))
if (deviceId) {
	def authString = "YWRtaW46a2Vha29l"	
	def from = getFormattedStr(pocetakCasa.toString())
	def to = getFormattedStr(krajCasa.toString())
	def url = baseUrl + "deviceId=$deviceId&as=$authString&from=$from&to=$to"
	def trackingUrlField = cfManager.getCustomFieldObject("customfield_11224")
	trackingUrlField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(trackingUrlField), url), changeHolder)
}


def getFormattedStr(dateTime) {
	def hour = dateTime.split(" ")[1].split(":")[0]
	def minute = dateTime.split(" ")[1].split(":")[1]

	return dateTime.split(" ")[0] + "T$hour" + ":$minute" + ":00Z"  
}
