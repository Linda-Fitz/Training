import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreirajPrakticnuObuku")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def projectManager = ComponentAccessor.getProjectManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def userUtil = ComponentAccessor.getUserUtil()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

// Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-612")

// Provjera da li vec postoji
Boolean toCreate = true
def counter = 0
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)){
    if (link.getLinkTypeId() == 10504 && link.getDestinationObject().getIssueTypeId() == "10304") {
      counter += 1
      if (link.getDestinationObject().getStatusId() != "10301") {
        toCreate = false
      }
    }
}

def brojKrugovaPrakticne = cfManager.getCustomFieldObject("customfield_11301")
brojKrugovaPrakticne.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojKrugovaPrakticne), (double)counter), changeHolder)

if (toCreate) {
  def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
  def searchProvider = ComponentAccessor.getComponent(SearchProvider)
  def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
 GroovyShell shell = new GroovyShell()
  def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
  String query = "issuetype = Konfiguracija AND status = aktivno"
  def queryIssue = shell.parse(new File("$basePath/general.groovy"))
  def configIssue = queryIssue.executeJQL(query)[0]

  brMinKonfField = cfManager.getCustomFieldObject("customfield_11404")
  ugovorenBrMinObukeField = cfManager.getCustomFieldObject("customfield_11400")
  preostaliMinRedField = cfManager.getCustomFieldObject("customfield_11403")
  brPreostalihCasRedField = cfManager.getCustomFieldObject("customfield_11226")

  def brMinKonf = configIssue.getCustomFieldValue(brMinKonfField) 
  def ugovorenBrMinObuke = configIssue.getCustomFieldValue(ugovorenBrMinObukeField)

  def preostaliMinRed = ugovorenBrMinObuke
  def brPreostalihCasRed = Math.ceil(preostaliMinRed / brMinKonf)

  def imeField = cfManager.getCustomFieldObject("customfield_10132")
  def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
  def ime = issue.getCustomFieldValue(imeField)
  def prezime = issue.getCustomFieldValue(prezimeField)
  def telefonField = cfManager.getCustomFieldObject("customfield_10220")
  def viberField = cfManager.getCustomFieldObject("customfield_10221")
  def emailField = cfManager.getCustomFieldObject("customfield_10222")
  def brojLicneField = cfManager.getCustomFieldObject("customfield_10216")
  def ocevoImeField = cfManager.getCustomFieldObject("customfield_10117")
  def userZaposleniField = cfManager.getCustomFieldObject("customfield_10207")
  def instruktorField = cfManager.getCustomFieldObject("customfield_10913")
  
  def telefon = issue.getCustomFieldValue(telefonField)
  def viber = issue.getCustomFieldValue(viberField)
  def email = issue.getCustomFieldValue(emailField)
  def brojLicne = issue.getCustomFieldValue(brojLicneField)
  def ocevoIme = issue.getCustomFieldValue(ocevoImeField)
  def instruktor = issue.getCustomFieldValue(instruktorField)
  def user1 = instruktor.getCustomFieldValue(userZaposleniField)
  def sum = "Prakti\u010dna obuka | $ime $prezime" 

  MutableIssue prakticnaObukaIssue = issueFactory.getIssue()
  prakticnaObukaIssue.setSummary(sum)
  prakticnaObukaIssue.setProjectObject(KAN)
  prakticnaObukaIssue.setIssueTypeId("10304")
  prakticnaObukaIssue.setAssignee(user1)
  prakticnaObukaIssue.setReporter(currentUser)
  prakticnaObukaIssue.setCustomFieldValue(imeField, ime)
  prakticnaObukaIssue.setCustomFieldValue(prezimeField, prezime)
  prakticnaObukaIssue.setCustomFieldValue(telefonField, telefon)
  prakticnaObukaIssue.setCustomFieldValue(viberField, viber)
  prakticnaObukaIssue.setCustomFieldValue(emailField, email)
  prakticnaObukaIssue.setCustomFieldValue(brojLicneField, brojLicne)
  prakticnaObukaIssue.setCustomFieldValue(ocevoImeField, ocevoIme)
  prakticnaObukaIssue.setCustomFieldValue(instruktorField, instruktor)
  prakticnaObukaIssue.setCustomFieldValue(ugovorenBrMinObukeField, ugovorenBrMinObuke)
  prakticnaObukaIssue.setCustomFieldValue(preostaliMinRedField, preostaliMinRed)
  //prakticnaObukaIssue.setCustomFieldValue(brPreostalihCasRedField, brPreostalihCasRed)
  prakticnaObukaIssue.setCustomFieldValue(brMinKonfField, brMinKonf)

  Map<String,Object> prakticnaObukaIssueParams = ["issue" : prakticnaObukaIssue] as Map<String,Object>
  issueManager.createIssueObject(visol, prakticnaObukaIssueParams)
  linkMgr.createIssueLink(issue.id, prakticnaObukaIssue.id, 10504, 1, visol)  

  brMinKonfField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brMinKonfField), brMinKonf), changeHolder)
} 
