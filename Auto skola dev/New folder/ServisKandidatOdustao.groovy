import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KandidatOdustao")
log.setLevel(Level.DEBUG)
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def odustaoField = cfManager.getCustomFieldObject("customfield_11502")

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = Kandidat AND status not in (\"10204\",\"10205\",\"10203\") AND updated <= startOfDay(-90)" //"SPREMAN ZA IZDAVANJE SVJEDOČANSTVA\",\"SVJEDOČANSTVO IZDATO\",\"SPREMAN ZA PRAKTIČNU OBUKU\"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)

log.debug(issues)
for (i = issues.size() - 1; i >= 0; i--) {
    MutableIssue odustaoKandidat = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issues[i].key)  
   def fieldConfig = odustaoField.getRelevantConfig(odustaoKandidat)
    def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'Da'}
    odustaoField.updateValue(null, odustaoKandidat, new ModifiedValue(odustaoKandidat.getCustomFieldValue(odustaoField), da), changeHolder)

}