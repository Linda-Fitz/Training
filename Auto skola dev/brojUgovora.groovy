import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.index.IssueIndexingService
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat
import com.atlassian.jira.user.ApplicationUser

 def log = Logger.getLogger("com.acme.Iteration")
 log.setLevel(Level.DEBUG)
 
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

MutableIssue issue = issue

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-785")

def changeHolder = new DefaultIssueChangeHolder();
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def brojUgovora = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10217")
def prefix

Date now = new Date(System.currentTimeMillis())
def nowFormatted = new SimpleDateFormat("MM-yyyy").format(now).toString()
def year = nowFormatted.split("-")[1]

String suffix = "/" + year
int counter
def query
def results
def vrstaField = cfManager.getCustomFieldObject("customfield_10419")
	def vrsta = issue.getCustomFieldValue(vrstaField).toString()
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String

if (!issue.getCustomFieldValue(brojUgovora) ) {
    if (issue.getCustomFieldValue(vrstaField).toString() == "Dopunski rad") {
		String queryActivate =  "Ugovor o radu\"  AND cf[10217] is not EMPTY AND cf[10419] = 'Dopunski rad' AND created >= startOfYear() "
	} else {
		String queryActivate = "Ugovor o radu\"  AND cf[10217] is not EMPTY AND cf[10419] != 'Dopunski rad' AND created >= startOfYear()"
	}
	
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issuesToChange = queryIssue.executeJQL(queryActivate)
counter = issuesToChange.size() + 1
     
	def counterStr = String.format("%06d", counter)
	def ugovorBroj = counterStr + suffix
    log.debug(ugovorBroj)
   // return ugovorBroj
	brojUgovora.updateValue(null,issue, new ModifiedValue(null, ugovorBroj),changeHolder)
		
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
def za = issue.getCustomFieldValue(brojUgovora)
def summary =  "Ugovor o radu |" + " $ime $prezime |" + " $ugovorBroj"

    log.debug(summary)
	
issue.setSummary(summary) 
issue.setAssignee(currentUser)
issue.setReporter(currentUser)
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)
issueIndexingService.reIndex(issueManager.getIssueObject(issue.id));
}
