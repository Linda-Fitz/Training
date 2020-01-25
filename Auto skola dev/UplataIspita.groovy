import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.UplataIspita")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-425")

ispitField = cfManager.getCustomFieldObject("customfield_10814")
ispit = issue.getCustomFieldValue(ispitField)
def uplacenoField = cfManager.getCustomFieldObject("customfield_10637")
def grupaIspita = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11221"))
uplaceno = (issue.getCustomFieldValue(uplacenoField)) ? issue.getCustomFieldValue(uplacenoField).toString() : ""

Issue prijavljenIspit

issue.getSubTaskObjects().each{ subtask ->
	if (subtask.getCustomFieldValue(ispitField) == ispit) {
		prijavljenIspit = subtask
	}
}

if (!prijavljenIspit) {	return }

// Tranzicija prijavljenog ispita
statusIdPrijavljenogIspita = prijavljenIspit.getStatusId()
def transition = checkUplata()
if (transition) {
	workflowTransitionUtil.setAction(checkUplata())	 // -> uplacen
	workflowTransitionUtil.setIssue(prijavljenIspit)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()
}

// Manual join grupi 
if (grupaIspita) {
	linkMgr.createIssueLink(issue.id, grupaIspita.id, 10502, 1, visol)
	if (transition) {
		workflowTransitionUtil.setAction(71)	// uplacen -> na spisku
		workflowTransitionUtil.setIssue(prijavljenIspit)
		workflowTransitionUtil.validate()
		workflowTransitionUtil.progress()		
	}
}

// Rekreiranje linka
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
	if (ispit != link.getDestinationObject()) {	continue }
	def linkTypeId = link.getId()
	if (linkTypeId != 10301 && uplaceno == "DA") {
		linkMgr.removeIssueLink(link, visol)
		recreateLink(issue, ispit)
	}
	if (linkTypeId != 10401 && uplaceno == "DOZVOLJENO") {
		linkMgr.removeIssueLink(link, visol)
		recreateLink(issue, ispit)
	}
	if (linkTypeId != 10300 && uplaceno == "NE") {
		linkMgr.removeIssueLink(link, visol)
		recreateLink(issue, ispit)
	}
}

def recreateLink(Issue issue, Issue ispit) {
	long linkId
	if (uplaceno == "DA") {
		linkId = 10301
	} else if (uplaceno == "DOZVOLJENO") {
		linkId = 10401
	} else {
		linkId = 10300
	}

	linkMgr.createIssueLink(issue.id, ispit.id, linkId, 1, visol)
}

def checkUplata() {	
	if (uplaceno == "DA") {
		if (statusIdPrijavljenogIspita == "10404") {
			return 21			
		} else if (statusIdPrijavljenogIspita == "10405") {
			return 31
		}
	} else if (uplaceno == "NE") {
		if (statusIdPrijavljenogIspita == "10404") {
			return 11			
		} 
	} else if (uplaceno == "DOZVOLJENO") {
		if (statusIdPrijavljenogIspita == "10404") {
			return 21			
		} else if (statusIdPrijavljenogIspita == "10405") {
			return 31
		}
	} else {
		return 11
	}
}