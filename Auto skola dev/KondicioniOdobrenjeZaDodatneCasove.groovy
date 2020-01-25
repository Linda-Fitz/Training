import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KondicioniOdobrenjeZaDodatneCasove")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

def brCasovaField = cfManager.getCustomFieldObject("customfield_10912")
def brojCasovaZaOdobrenje = cfManager.getCustomFieldObject("customfield_11276")
def cijenaCasaField = cfManager.getCustomFieldObject("customfield_10502")
def ugovorenaCijenaField = cfManager.getCustomFieldObject("customfield_10923")
zaPlatitiField = cfManager.getCustomFieldObject("customfield_11269")
def iznosUplateField = cfManager.getCustomFieldObject("customfield_10424")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
def voziloField = cfManager.getCustomFieldObject("customfield_10324")
def zeljenoTrCasaField = cfManager.getCustomFieldObject("customfield_10918")
def brojOdvezenihCasovaField = cfManager.getCustomFieldObject("customfield_11606")
def brojPreostalihCasovaField = cfManager.getCustomFieldObject("customfield_11226")

def brCasova1 = issue.getCustomFieldValue(brCasovaField) 
def brCasova2 = issue.getCustomFieldValue(brojCasovaZaOdobrenje)
def cijenaCasa = issue.getCustomFieldValue(cijenaCasaField)
def ugovorenaCijena = issue.getCustomFieldValue(ugovorenaCijenaField)
def zaPlatiti = issue.getCustomFieldValue(zaPlatitiField)
def iznosUplate = issue.getCustomFieldValue(iznosUplateField)
def preostalo = issue.getCustomFieldValue(preostaloField)
def vozilo = issue.getCustomFieldValue(voziloField)
def zeljenoTrCasa = issue.getCustomFieldValue(zeljenoTrCasaField)
def brojOdvezenihCasova = issue.getCustomFieldValue(brojOdvezenihCasovaField)

if (ugovorenaCijena) {
//double calcCijenaCasa1 = cijenaIssue.getCustomFieldValue(cijenaCasaField).toDouble() * zeljenoTrCasa.toDouble() / 45
  double brCasova = brCasova1 + brCasova2
  double brojPreostalihCasova = brCasova - brojOdvezenihCasova
  def novoZaPlatiti = ugovorenaCijena * brCasova
  def razlika = novoZaPlatiti - zaPlatiti.toDouble()
  zaPlatitiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zaPlatitiField), novoZaPlatiti.round(2)),changeHolder)
  //cijenaCasaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cijenaCasaField), calcCijenaCasa1.round(2).toString()),changeHolder)
  //preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostalo + razlika),changeHolder)
  brCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brCasovaField), brCasova.round(2)),changeHolder)
  brojPreostalihCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPreostalihCasovaField), brojPreostalihCasova.round(2)),changeHolder)
} else {
  def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
  def searchProvider = ComponentAccessor.getComponent(SearchProvider)
  //def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
  def cijenaIssue
  def query
  def queryIssue
  GroovyShell shell = new GroovyShell()
  def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
  if (vozilo) {
    query = "issuetype = Cjenovnik AND status in (aktivno) AND cf[10810] in ($vozilo)"
    queryIssue = shell.parse(new File("$basePath/general.groovy"))
    results = queryIssue.executeJQL(query)

  }

  if (!cijenaIssue) {
    query = "issuetype = Cjenovnik AND status in (aktivno) AND cf[10810] is EMPTY"
    queryIssue = shell.parse(new File("$basePath/general.groovy"))
    results = queryIssue.executeJQL(query)

  }

  if (!cijenaIssue) { return }
  
  def defaultTrCasa = cijenaIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10917"))
  double calcCijenaCasa = cijenaIssue.getCustomFieldValue(cijenaCasaField).toDouble() * zeljenoTrCasa.toDouble() / defaultTrCasa.toDouble()
  double novoZaPlatiti = calcCijenaCasa * brCasova
  def razlika = novoZaPlatiti.toDouble() - zaPlatiti.toDouble()
  double brCasova = brCasova1.toDouble() + brCasova2.toDouble()

  cijenaCasaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cijenaCasaField), calcCijenaCasa.round(2).toString()),changeHolder)
  zaPlatitiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zaPlatitiField), novoZaPlatiti.round(2)),changeHolder)
  brCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brCasovaField), brCasova.round(2)),changeHolder)
  brojPreostalihCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPreostalihCasovaField), brojPreostalihCasova.round(2)),changeHolder)
  //preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), (preostalo + razlika).round(2)),changeHolder)
}

def countGratis(parent) {
  def count = 0
  def subtasks = parent.getSubTaskObjects()
  if (subtasks) {
    subtasks.each {
      if (it.getCustomFieldValue(zaPlatitiField) == "0") {
        count += 1
      }
    }   
  }
  return count
}