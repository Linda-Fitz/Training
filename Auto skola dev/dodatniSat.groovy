import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;

import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.CasObuke")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def projectManager = ComponentAccessor.getProjectManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-167")

def imeField = cfManager.getCustomFieldObject("customfield_10132")
def ocevoField = cfManager.getCustomFieldObject("customfield_10117")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def viberField = cfManager.getCustomFieldObject("customfield_10221")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def licnaispravaField = cfManager.getCustomFieldObject("customfield_10216")
def zakazanpocetakField = cfManager.getCustomFieldObject("customfield_10919")
def trajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def zakazankrajField = cfManager.getCustomFieldObject("customfield_10920")
def posaljizahtjevField = cfManager.getCustomFieldObject("customfield_10922")
def userZaposleniField = cfManager.getCustomFieldObject("customfield_10207")
def instruktorField = cfManager.getCustomFieldObject("customfield_10913")
def voziloField = cfManager.getCustomFieldObject("customfield_10324")

def ime = issue.getCustomFieldValue(imeField)
def ocevo = issue.getCustomFieldValue(ocevoField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)
def viber = issue.getCustomFieldValue(viberField)
def email = issue.getCustomFieldValue(emailField)
def isprava = issue.getCustomFieldValue(licnaispravaField)
def pocetak = issue.getCustomFieldValue(zakazanpocetakField)
def trajanje = issue.getCustomFieldValue(trajanjeField)
def kraj = issue.getCustomFieldValue(zakazankrajField)
def zahtjev = issue.getCustomFieldValue(posaljizahtjevField)
def instruktor = issue.getCustomFieldValue(instruktorField)
def vozilo = issue.getCustomFieldValue(voziloField)
def user1 = instruktor.getCustomFieldValue(userZaposleniField)

String type = "Dodatni \u010das"

def pocetakStr = pocetak.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]
def krajStr = kraj.toString()
def krajHour = krajStr.split(" ")[1].split(":")[0]
def krajMin = krajStr.split(" ")[1].split(":")[1]

def sum = "Dodatni \u010das vo\u017enje | ${ime} ${prezime} | $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h" 
log.debug(sum)

MutableIssue casIssue = issueFactory.getIssue()
casIssue.setSummary(sum)
casIssue.setParentObject(issue)
casIssue.setProjectObject(KAN)
casIssue.setIssueTypeId("10601")
casIssue.setAssignee(user1)
casIssue.setReporter(visol)
casIssue.setCustomFieldValue(imeField, ime)
casIssue.setCustomFieldValue(ocevoField, ocevo)
casIssue.setCustomFieldValue(prezimeField, prezime)
casIssue.setCustomFieldValue(telefonField, telefon)
casIssue.setCustomFieldValue(viberField, viber)
casIssue.setCustomFieldValue(emailField, email)
casIssue.setCustomFieldValue(licnaispravaField, isprava)
casIssue.setCustomFieldValue(zakazanpocetakField, pocetak)
casIssue.setCustomFieldValue(trajanjeField, trajanje)
casIssue.setCustomFieldValue(zakazankrajField, kraj)
casIssue.setCustomFieldValue(posaljizahtjevField, zahtjev)
casIssue.setCustomFieldValue(voziloField, vozilo)

Map<String,Object> casIssueParams = ["issue" : casIssue] as Map<String,Object>
issueManager.createIssueObject(visol, casIssueParams)
subTaskManager.createSubTaskIssueLink(issue, casIssue, visol)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
//def parentIssue = (MutableIssue)issue.getParentObject() 

def posaljiZahtjev = issue.getCustomFieldValue(posaljizahtjevField).toString()


    
if(posaljiZahtjev == "DA"){
    
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
        workflowTransitionUtil.setIssue(casIssue);
        workflowTransitionUtil.setAction(31);  
        def sum1 = "Dodatni \u010das vo\u017enje | ${ime} ${prezime} | $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h"
        casIssue.setSummary(sum1)
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
  
}   

      

//trajanjeField.updateValue(null, casIssue, new ModifiedValue(casIssue.getCustomFieldValue(trajanjeField), trajanje),changeHolder)
//prisutniKandidatiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prisutniKandidatiField), null),changeHolder)
//datumVrijemeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumVrijemeField), null),changeHolder)

