import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajKupon")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def projectManager = ComponentAccessor.getProjectManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

//Issue issue = issueManager.getIssueByIgnoreCase("KAN-4208")

def brojKuponaField = cfManager.getCustomFieldObject("customfield_11234")
def datumIzdavanjaField = cfManager.getCustomFieldObject("customfield_11238")
def datumIstekaField = cfManager.getCustomFieldObject("customfield_11239")
def popustField = cfManager.getCustomFieldObject("customfield_11240")
def serijskiField = cfManager.getCustomFieldObject("customfield_11229")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")

def brojKupona = issue.getCustomFieldValue(brojKuponaField)
def datumIzdavanja = issue.getCustomFieldValue(datumIzdavanjaField)
def datumIsteka = issue.getCustomFieldValue(datumIstekaField)
def popust = issue.getCustomFieldValue(popustField)
def serijski = issue.getCustomFieldValue(serijskiField)
def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)

def sum = serijski
def issueTypeId = "10900" //Kupon

MutableIssue kuponIssue = issueFactory.getIssue()
kuponIssue.setSummary("Kupon | " + ime + " " + prezime + " | " + sum) 
kuponIssue.setParentObject(issue)
kuponIssue.setProjectObject(KAN)
kuponIssue.setIssueTypeId(issueTypeId)
kuponIssue.setCustomFieldValue(brojKuponaField, brojKupona)
kuponIssue.setCustomFieldValue(datumIzdavanjaField, datumIzdavanja)
kuponIssue.setCustomFieldValue(datumIstekaField, datumIsteka)
kuponIssue.setCustomFieldValue(popustField, popust)
kuponIssue.setCustomFieldValue(serijskiField, serijski)
kuponIssue.setAssignee(currentUser)
kuponIssue.setReporter(currentUser)

Map<String,Object> kuponIssueParams = ["issue" : kuponIssue] as Map<String,Object>
issueManager.createIssueObject(visol, kuponIssueParams)
subTaskManager.createSubTaskIssueLink(issue, kuponIssue, visol)