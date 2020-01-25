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

def log = Logger.getLogger("com.acme.PromenaTerminaSummaryUpdate")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-89")
log.debug(issue.summary)

def datumIVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def novitermin = issue.getCustomFieldValue(datumIVrijemeField) 

def pocetakStr = novitermin.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]

def sum = "Teorijska obuka | " + "${day}-${month}-${year} ${hour}:${minute}h"
log.debug(sum)

issue.setSummary(sum)
issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false)

