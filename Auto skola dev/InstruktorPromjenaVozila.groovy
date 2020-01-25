import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.ImportUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.user.ApplicationUser

log = Logger.getLogger("com.acme.InstruktorVoziloLinkListener")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("CE-1175")

def staroVozilo = getStaroVozilo(issue)

def instruktoriField = cfManager.getCustomFieldObject("customfield_10403")
def instruktoriNaStarom = staroVozilo?.getCustomFieldValue(instruktoriField)
if (instruktoriNaStarom?.contains(issue)) {
  instruktoriNaStarom.remove(instruktoriNaStarom.indexOf(issue)) 
  instruktoriField.updateValue(null, staroVozilo, new ModifiedValue(staroVozilo.getCustomFieldValue(instruktoriField), instruktoriNaStarom), new DefaultIssueChangeHolder())  
  
}

if (staroVozilo) {
  issueIndexingService.reIndex(issueManager.getIssueObject(staroVozilo.id))
  log.debug("${staroVozilo.key} reindexovano")
}

// Dodavanje na novom
def novoVozilo = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324"))
def instruktoriNaNovom = novoVozilo.getCustomFieldValue(instruktoriField)
if (!instruktoriNaNovom) {
instruktoriNaNovom = [issue]
  instruktoriField.updateValue(null, novoVozilo, new ModifiedValue(novoVozilo.getCustomFieldValue(instruktoriField), instruktoriNaNovom), new DefaultIssueChangeHolder())
} else if (!instruktoriNaNovom.contains(issue)) {
  instruktoriNaNovom.add(issue)
  instruktoriField.updateValue(null, novoVozilo, new ModifiedValue(novoVozilo.getCustomFieldValue(instruktoriField), instruktoriNaNovom), new DefaultIssueChangeHolder())
}

boolean wasIndexing = ImportUtils.isIndexIssues();
ImportUtils.setIndexIssues(true);
log.debug("${novoVozilo.key} reindexovano")
issueIndexingService.reIndex(issueManager.getIssueObject(novoVozilo.id))
ImportUtils.setIndexIssues(wasIndexing)

def getStaroVozilo(Issue issue) {
  def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
  def searchProvider = ComponentAccessor.getComponent(SearchProvider)
  GroovyShell shell = new GroovyShell()
  def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
  String query = "issuetype = \"vozila\" AND cf[10403] = ${issue.key}"
  def queryIssue = shell.parse(new File("$basePath/general.groovy"))
  def issues = queryIssue.executeJQL(query)

  if (issues.size() == 1) { return issues[0] }
}

