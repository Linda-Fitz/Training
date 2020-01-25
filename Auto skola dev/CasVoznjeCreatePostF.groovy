import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
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
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3183")
def parentIssue = issue.getParentObject()
def kandidatIssue
def vozilo
for (IssueLink link in linkMgr.getInwardLinks(parentIssue.id)){
  	if (link.getLinkTypeId() == 10504 && link.getSourceObject().getIssueTypeId() == "10200") {
  		kandidatIssue = link.getSourceObject()
  		vozilo = kandidatIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324")) // Vozilo issue field
  	}
}

def ugovorenBrMinObukeField = cfManager.getCustomFieldObject("customfield_11400")
def odvezenBrMinRedField = cfManager.getCustomFieldObject("customfield_11306")
def obrBrMinRedField = cfManager.getCustomFieldObject("customfield_11401")
def preostaliMinRedField = cfManager.getCustomFieldObject("customfield_11403")
def brPreostalihCasRedField = cfManager.getCustomFieldObject("customfield_11226")
def odobrenBrDopField = cfManager.getCustomFieldObject("customfield_10912")
def odobrBrMinDopField = cfManager.getCustomFieldObject("customfield_11405")
def odvezenBrMinDopField = cfManager.getCustomFieldObject("customfield_11406")
def obrBrMinDopField = cfManager.getCustomFieldObject("customfield_11407")
def zaPlatitiDopField = cfManager.getCustomFieldObject("customfield_11303")
def tolerancijaKonfField = cfManager.getCustomFieldObject("customfield_11402")
def brMinKonfField = cfManager.getCustomFieldObject("customfield_11404")
def cijenaDopunskogCasaField =  cfManager.getCustomFieldObject("customfield_10924")
def osnovnoTrajanjeField = cfManager.getCustomFieldObject("customfield_10917")
def zaPlatitiField = cfManager.getCustomFieldObject("customfield_11256")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def vrstaPlacanjaField = cfManager.getCustomFieldObject("customfield_10320")
def pocetakCasaField = cfManager.getCustomFieldObject("customfield_10919")
def krajCasaField = cfManager.getCustomFieldObject("customfield_10920")
def trajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def tipCasaVoznjeField = cfManager.getCustomFieldObject("customfield_11228")
def voziloField = cfManager.getCustomFieldObject("customfield_10324")

def vrstaPlacanja = kandidatIssue.getCustomFieldValue(vrstaPlacanjaField).toString()
def pocetakCasa = issue.getCustomFieldValue(pocetakCasaField)
def krajCasa = issue.getCustomFieldValue(krajCasaField)
def trajanje = issue.getCustomFieldValue(trajanjeField)
def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)
def vozilo1 = kandidatIssue.getCustomFieldValue(voziloField)

// CONFIG JQL
def queryConfig = "issuetype = Konfiguracija AND status = aktivno"
def resultsConfig = searchProvider.search(queryConfig, visol, PagerFilter.getUnlimitedFilter())
def configIssue = resultsConfig.getIssues()[0]

def tolerancija = configIssue.getCustomFieldValue(tolerancijaKonfField) // % tolerancije
def brMinKonf = configIssue.getCustomFieldValue(brMinKonfField)	 // br. minuta po casu
def ugovorenBrMinObuke = parentIssue.getCustomFieldValue(ugovorenBrMinObukeField) // ukupno minuta

// SUMMARY
def sum
def pocetakStr = pocetakCasa.toString()
def year = pocetakStr.split("-")[0]
def month = pocetakStr.split("-")[1]
def day = pocetakStr.split("-")[2].split(" ")[0]
def hour = pocetakStr.split(" ")[1].split(":")[0]
def minute = pocetakStr.split(" ")[1].split(":")[1]
def krajStr = krajCasa.toString()
def krajHour = krajStr.split(" ")[1].split(":")[0]
def krajMin = krajStr.split(" ")[1].split(":")[1]

def fieldConfig = tipCasaVoznjeField.getRelevantConfig(issue)
if (issue.getIssueTypeId() == "10703") { // REDOVNI
	sum = "Redovni \u010das vo\u017enje | $ime $prezime | $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h" 	
} else if (issue.getIssueTypeId() == "10802") { // DOPUNSKI
	sum = "Dopunski \u010das vo\u017enje | $ime $prezime | $day.$month.$year $hour:$minute" + "h-$krajHour:$krajMin" + "h"
}
issue.setSummary(sum)
issue.setAssignee(currentUser)
issue.setReporter(currentUser)
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)


