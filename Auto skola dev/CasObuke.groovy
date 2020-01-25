import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.CasObuke")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def projectManager = ComponentAccessor.getProjectManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

UserUtil userUtil = ComponentAccessor.getUserUtil()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-167")

def predavacField = cfManager.getCustomFieldObject("customfield_10609")
def predavacPPField = cfManager.getCustomFieldObject("customfield_10633")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10618")
def prisutniKandidatiLinkField = cfManager.getCustomFieldObject("customfield_10824")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")

def predavac = issue.getCustomFieldValue(predavacField)
def predavacPP = issue.getCustomFieldValue(predavacPPField)
def datumVrijeme = issue.getCustomFieldValue(datumVrijemeField)
def prisutniKandidati = issue.getCustomFieldValue(prisutniKandidatiField)

def datetimestr = datumVrijeme.toString()
datetimestr = datetimestr.substring(0, datetimestr.size() - datetimestr.split(":")[-1].size() - 1) + " h"
def ime = predavac.getCustomFieldValue(imeField)
def prezime = predavac.getCustomFieldValue(prezimeField)
def telefon = predavac.getCustomFieldValue(telefonField)

def sum = "Sat obuke | $datetimestr | $ime $prezime $telefon"

MutableIssue casIssue = issueFactory.getIssue()
casIssue.setSummary(sum)
casIssue.setParentObject(issue)
casIssue.setProjectObject(KAN)
casIssue.setIssueTypeId("10401")
casIssue.setCustomFieldValue(prisutniKandidatiField, prisutniKandidati)
casIssue.setCustomFieldValue(datumVrijemeField, datumVrijeme)
casIssue.setCustomFieldValue(predavacField, predavac)
casIssue.setCustomFieldValue(predavacPPField, predavacPP)
casIssue.setAssignee(currentUser)
casIssue.setReporter(currentUser)
Map<String,Object> casIssueParams = ["issue" : casIssue] as Map<String,Object>
issueManager.createIssueObject(visol, casIssueParams)
subTaskManager.createSubTaskIssueLink(issue, casIssue, visol)

prisutniKandidatiLinkField.updateValue(null, casIssue, new ModifiedValue(casIssue.getCustomFieldValue(prisutniKandidatiField), prisutniKandidati),changeHolder)
prisutniKandidatiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prisutniKandidatiField), null),changeHolder)
datumVrijemeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumVrijemeField), null),changeHolder)