import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajCasTeorije")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
issueFactory = ComponentAccessor.getIssueFactory()
projectManager = ComponentAccessor.getProjectManager()
subTaskManager = ComponentAccessor.getSubTaskManager()
changeHolder = new DefaultIssueChangeHolder()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

predavacField = cfManager.getCustomFieldObject("customfield_10609")
predavacPPField = cfManager.getCustomFieldObject("customfield_10633")
datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10824")
predavac = issue.getCustomFieldValue(predavacField)
predavacPP = issue.getCustomFieldValue(predavacPPField)
def vrijemeCasa = issue.getCustomFieldValue(datumVrijemeField)
def prisutniKandidati = issue.getCustomFieldValue(prisutniKandidatiField)

log.debug("predavac: " + predavac)

pocetakStr = vrijemeCasa.toString()
year = pocetakStr.split("-")[0]
month = pocetakStr.split("-")[1]
day = pocetakStr.split("-")[2].split(" ")[0]
hour = pocetakStr.split(" ")[1].split(":")[0]
minute = pocetakStr.split(" ")[1].split(":")[1]
sum = "\u010cas teorija | $day.$month.$year $hour:$minute" + "h | " + predavac

def empty = []
issue.setCustomFieldValue(prisutniKandidatiField, empty)
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)

MutableIssue casIssue = issueFactory.getIssue()
casIssue.setSummary(sum)
casIssue.setParentObject(issue)
log.debug(casIssue)
casIssue.setProjectObject(KAN)
casIssue.setIssueTypeId("10401")
casIssue.setCustomFieldValue(datumVrijemeField, vrijemeCasa)		
casIssue.setCustomFieldValue(predavacField, predavac)
casIssue.setCustomFieldValue(predavacPPField, predavacPP)
casIssue.setCustomFieldValue(prisutniKandidatiField, prisutniKandidati)
//casIssue.setAssignee(currentUser)
//casIssue.setReporter(currentUser)
Map<String,Object> casIssueParams = ["issue" : casIssue] as Map<String,Object>
issueManager.createIssueObject(visol, casIssueParams)
subTaskManager.createSubTaskIssueLink(issue, casIssue, visol)
