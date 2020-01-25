import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.Issue
import java.sql.Timestamp
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.PromenaTerminaSummaryUpdateGrupa")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-90")

log.debug("Stari summary: " + issue.summary)
def string = issue.summary

def prviDeo = string.split(" ")[0]
log.debug(prviDeo)
def drugiDeo = string.split(" ")[1]

def stringo = prviDeo +" " + drugiDeo 
log.debug(stringo)

def datumIVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def novitermin = issue.getCustomFieldValue(datumIVrijemeField) 

def pocetakStr = novitermin.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]

def sum = "$stringo ${hour}:${minute}h"
log.debug(sum)

issue.setSummary(sum)
issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false)

