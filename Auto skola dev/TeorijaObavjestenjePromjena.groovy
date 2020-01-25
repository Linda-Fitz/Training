import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import java.sql.Timestamp
import org.apache.log4j.Level
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.ObukaZavrsena")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-389")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def obavjestenjeField = cfManager.getCustomFieldObject("customfield_10610")
def datumIVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def obavjestenje = issue.getCustomFieldValue(obavjestenjeField)
def grupaIssue = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11218"))
def novitermin = issue.getCustomFieldValue(datumIVrijemeField)

if (!novitermin || !grupaIssue) { return }

grupaIssue.setCustomFieldValue(obavjestenjeField, obavjestenje)

workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction(61)   
workflowTransitionUtil.setIssue(grupaIssue)
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()

def pocetakStr = novitermin.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]
def sum = "Grupa " + grupaIssue.summary.split(" ")[1] + " $hour:$minute" + "h"
grupaIssue.setSummary(sum)

grupaIssue.setAssignee(currentUser)
grupaIssue.setReporter(currentUser)

datumIVrijemeField.updateValue(null, grupaIssue, new ModifiedValue(grupaIssue.getCustomFieldValue(datumIVrijemeField), novitermin), changeHolder)
issueManager.updateIssue(null, grupaIssue, EventDispatchOption.ISSUE_UPDATED, false)

def casovi = grupaIssue.getSubTaskObjects().findAll { it.getStatusId() == "10501" }
casovi.sort { a,b -> a.getCustomFieldValue(datumIVrijemeField) <=> b.getCustomFieldValue(datumIVrijemeField) }

casovi.each { subtask ->
	pocetakStr = novitermin.toString()
	year = pocetakStr.split("-")[0]
	month = pocetakStr.split("-")[1]
	day = pocetakStr.split("-")[2].split(" ")[0]
	hour = pocetakStr.split(" ")[1].split(":")[0]
	minute = pocetakStr.split(" ")[1].split(":")[1]
	sum = "\u010cas teorija $day.$month.$year $hour:$minute" + "h" 
	subtask.setSummary(sum)
	datumIVrijemeField.updateValue(null, subtask, new ModifiedValue(subtask.getCustomFieldValue(datumIVrijemeField), novitermin), changeHolder)
	issueManager.updateIssue(null, subtask, EventDispatchOption.ISSUE_UPDATED, false)
	novitermin += 1
}

