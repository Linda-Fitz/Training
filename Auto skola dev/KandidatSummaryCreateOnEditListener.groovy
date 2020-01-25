if (issue.getIssueTypeId() != '10200') { return } //kandidat
if (issue.getStatusId() != '10102') { return } //otvoreno

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KandidatSummaryCreate")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueService = ComponentAccessor.getIssueService()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
//MutableIssue issue = (MutableIssue) ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-3501")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()


MutableIssue issue = issue

def cfManager = ComponentAccessor.getCustomFieldManager()

def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def ime = issue.getCustomFieldValue(imeField)
if(!ime){
  ime = ""
}
def prezime = issue.getCustomFieldValue(prezimeField)
if(!prezime){
  prezime = ""
}
def brojField = cfManager.getCustomFieldObject("customfield_10220")
def broj = issue.getCustomFieldValue(brojField)
if(!broj){
  broj = ""
}

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = \"Konfiguracija\" AND status = \"AKTIVNO\""
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)


/*if (issues.size() > 0) {
  def defaultAssignee = issues[0].getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11305"))
  if (defaultAssignee) {
    def validateAssignResult = issueService.validateAssign(visol, issue.id, defaultAssignee.name)
    issueService.assign(visol, validateAssignResult)
  }
}*/

issue.setSummary(" Kandidat | $ime $prezime | $broj") 
issue.setAssignee(currentUser)
issue.setReporter(currentUser)
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)