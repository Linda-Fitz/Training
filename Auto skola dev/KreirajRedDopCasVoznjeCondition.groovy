import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajRedDodCasCondition")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//def issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-3856")

def brOdobrenihCasovaField = cfManager.getCustomFieldObject("customfield_10912")
def brOdobrenihMinutaField = cfManager.getCustomFieldObject("customfield_11405")
def odvezenBrMinDopField = cfManager.getCustomFieldObject("customfield_11406")
def brPreostalihCasRedField = cfManager.getCustomFieldObject("customfield_11226")

def kandidatLink = linkMgr.getInwardLinks(issue.id).find { it.getLinkTypeId() == 10504 && it.getSourceObject().getIssueTypeId() == "10200" }
def kandidatIssue = kandidatLink.getSourceObject()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

if (kandidatIssue.getStatusId() == "10102") {
	passesCondition = true
} else if (kandidatIssue.getStatusId() == "10202" || kandidatIssue.getStatusId() == "10203" ) { // VOZNJA- OBUKA,SPREMAN ZA PRAKTICNU OBUKU
	def brPreostalihCasRed = issue.getCustomFieldValue(brPreostalihCasRedField)
	if (brPreostalihCasRed > 0) { 
		passesCondition = true
	} else {
		passesCondition = false
	}
} else if (kandidatIssue.getStatusId() == "10309") { // VOZNJA- DODATNA OBUKA
	def brOdobrenihMinuta = issue.getCustomFieldValue(brOdobrenihMinutaField)
	def odvezenBrMinDop = (issue.getCustomFieldValue(odvezenBrMinDopField)) ? issue.getCustomFieldValue(odvezenBrMinDopField) : 0
	if (brOdobrenihMinuta > odvezenBrMinDop) { 
		passesCondition = true
	} else {
		passesCondition = false
	}
} else {
	passesCondition = false
}