import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.index.IssueIndexingService
import org.apache.log4j.Logger
import org.apache.log4j.Level

if (issue.getIssueTypeId() != "10101") { return }

def voziloChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find { it.field == "Vozilo" }
if (!voziloChanged) { return }

def log = Logger.getLogger("com.acme.Iteration")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
def changeHolder = new DefaultIssueChangeHolder();
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
// Issue issue = issueManager.getIssueByKeyIgnoreCase("DRUM-5087")

def voziloField = cfManager.getCustomFieldObject("customfield_10324")
def voziloIssue = issueManager.getIssueByKeyIgnoreCase(voziloChanged.newstring)
def key = issue.key
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype in (\"Kandidat\",\"Kondicioni kandidat\") AND cf[10913] = $key AND statusCategory != Done"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)

issues.each { 
  def kandidat = issueManager.getIssueByKeyIgnoreCase(it.key)
  voziloField.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(voziloField), voziloIssue), new DefaultIssueChangeHolder())
}