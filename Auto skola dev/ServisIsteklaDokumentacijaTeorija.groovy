import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import org.apache.log4j.Logger
import org.apache.log4j.Level

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
def log = Logger.getLogger("com.acme.IstekDokumentacije")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def dokumentacijaField = cfManager.getCustomFieldObject("customfield_11501")

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = Kandidat AND cf[10631] <= startOfDay()"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issuesT = queryIssue.executeJQL(query)

log.debug(issuesT)

for (i = issuesT.size() - 1; i >= 0; i--) {
    MutableIssue istekliKandidat = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issuesT[i].key)
    def fieldConfig = dokumentacijaField.getRelevantConfig(istekliKandidat)
    def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'Ne'}
    dokumentacijaField.updateValue(null, istekliKandidat, new ModifiedValue(istekliKandidat.getCustomFieldValue(dokumentacijaField), da), changeHolder)
}