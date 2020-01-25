import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajCasTeorije")
log.setLevel(Level.DEBUG)


def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def nameField = cfManager.getCustomFieldObject("customfield_10132")
def lastNameField = cfManager.getCustomFieldObject("customfield_10133")
def predavacField = cfManager.getCustomFieldObject("customfield_10609")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def predavac = issue.getCustomFieldValue(predavacField)
def predavacStr = predavac.getCustomFieldValue(nameField) + " " + predavac.getCustomFieldValue(lastNameField)
def vrijemeCasa = issue.getCustomFieldValue(datumVrijemeField)

pocetakStr = vrijemeCasa.toString()
year = pocetakStr.split("-")[0]
month = pocetakStr.split("-")[1]
day = pocetakStr.split("-")[2].split(" ")[0]
hour = pocetakStr.split(" ")[1].split(":")[0]
minute = pocetakStr.split(" ")[1].split(":")[1]
sum = "\u010cas teorija | $day.$month.$year $hour:$minute" + "h | " + predavacStr

issue.setSummary(sum)

issue.setAssignee(currentUser)
issue.setReporter(currentUser)
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)