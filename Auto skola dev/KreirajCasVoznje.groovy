import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
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
def subTaskManager = ComponentAccessor.getSubTaskManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

def brPreostalihCasRedField = cfManager.getCustomFieldObject("customfield_11226")
def pocetakCasaField = cfManager.getCustomFieldObject("customfield_10919")
def krajCasaField = cfManager.getCustomFieldObject("customfield_10920")
def trajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def tipCasaVoznjeField = cfManager.getCustomFieldObject("customfield_11228")
def brojCasovaVoznjeField = cfManager.getCustomFieldObject("customfield_11226")
def ukupnoTrajanjeField = cfManager.getCustomFieldObject("customfield_11227")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def voziloField = cfManager.getCustomFieldObject("customfield_10324")
def trajanje = issue.getCustomFieldValue(trajanjeField)
def pocetakCasa = issue.getCustomFieldValue(pocetakCasaField)
def krajCasa = issue.getCustomFieldValue(krajCasaField)
def baseUkupnoTrajanje = issue.getCustomFieldValue(ukupnoTrajanjeField) ?: (double)0
def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)

def kandidatLink = linkMgr.getInwardLinks(issue.id).find { it.getLinkTypeId() == 10504 && it.getSourceObject().getIssueTypeId() == "10200" }
def kandidatIssue = kandidatLink.getSourceObject()
def vozilo = kandidatIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324"))
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()


def pocetakStr = pocetakCasa.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]
def krajStr = krajCasa.toString()
def krajHour = krajStr.split(" ")[1].split(":")[0]
def krajMin = krajStr.split(" ")[1].split(":")[1]
def sum
def issueTypeId
if (kandidatIssue.getStatusId() == "10309") { // VOŽNJA- DODATNA OBUKA
	issueTypeId = "10802"
	sum = "Dopunski \u010das vo\u017enje $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h"  
} else if (kandidatIssue.getStatusId() == "10202") { // VOŽNJA- OBUKA
	issueTypeId = "10703"
	sum = "Redovni \u010das vo\u017enje $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h"  
} else if (kandidatIssue.getStatusId() == "10203") { // spreman za prakticnu
	issueTypeId = "10703"
	sum = "Redovni \u010das vo\u017enje $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h"  
}

if (!issueTypeId) { return }

MutableIssue casVoznjeIssue = issueFactory.getIssue()
casVoznjeIssue.setSummary(sum) 
casVoznjeIssue.setParentObject(issue)
casVoznjeIssue.setProjectObject(KAN)
casVoznjeIssue.setIssueTypeId(issueTypeId)
casVoznjeIssue.setReporter(currentUser)
casVoznjeIssue.setAssignee(currentUser)
casVoznjeIssue.setCustomFieldValue(pocetakCasaField, pocetakCasa)
casVoznjeIssue.setCustomFieldValue(krajCasaField, krajCasa)
casVoznjeIssue.setCustomFieldValue(trajanjeField, trajanje)

def viberField = cfManager.getCustomFieldObject("customfield_10221")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def brojLicneField = cfManager.getCustomFieldObject("customfield_10216")
def ocevoImeField = cfManager.getCustomFieldObject("customfield_10117")

def viber = issue.getCustomFieldValue(viberField)
def email = issue.getCustomFieldValue(emailField)
def brojLicne = issue.getCustomFieldValue(brojLicneField)
def ocevoIme = issue.getCustomFieldValue(ocevoImeField)

casVoznjeIssue.setCustomFieldValue(voziloField, vozilo)
casVoznjeIssue.setCustomFieldValue(imeField, ime)
casVoznjeIssue.setCustomFieldValue(prezimeField, prezime)
casVoznjeIssue.setCustomFieldValue(telefonField, telefon)
casVoznjeIssue.setCustomFieldValue(viberField, viber)
casVoznjeIssue.setCustomFieldValue(emailField, email)
casVoznjeIssue.setCustomFieldValue(brojLicneField, brojLicne)
casVoznjeIssue.setCustomFieldValue(ocevoImeField, ocevoIme)

Map<String,Object> casVoznjeIssueParams = ["issue" : casVoznjeIssue] as Map<String,Object>
issueManager.createIssueObject(visol, casVoznjeIssueParams)
subTaskManager.createSubTaskIssueLink(issue, casVoznjeIssue, visol)