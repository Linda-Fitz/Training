import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat
def log = Logger.getLogger("com.acme.ZavodniBroj")
log.setLevel(Level.DEBUG)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
def changeHolder = new DefaultIssueChangeHolder();
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-4148")

def zavodniBroj = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10321")
def datum = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10315")
def prefix
def cfManager = ComponentAccessor.getCustomFieldManager()
log.debug(issue.getCustomFieldValue(datum))
Date date = new Date(issue.getCustomFieldValue(datum).getTime())
def dateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(date).toString()
log.debug(dateFormatted)
def year = dateFormatted.split("/")[2].toInteger()
log.debug(year)
def sad = new Date()
def thisYear = sad.toString().split(" ")[-1].toInteger()

def razlika = (thisYear - year).toString()
log.debug(razlika)
String suffix = "/" + year

if (!issue.getCustomFieldValue(zavodniBroj)) {

    GroovyShell shell = new GroovyShell()
    def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
    String query = "issuetype = Kandidat AND cf[10321] is not EMPTY AND \"Datum\" <= endOfYear(-$razlika) AND \"Datum\" >= startOfYear(-$razlika)"
    def queryIssue = shell.parse(new File("$basePath/general.groovy"))
    def results = queryIssue.executeJQL(query)
	/*
    query =  jqlQueryParser.parseQuery("issuetype = Kandidat AND cf[10321] is not EMPTY AND \"Datum\" <= endOfYear(-$razlika) AND \"Datum\" >= startOfYear(-$razlika)") 
    results = searchProvider.search(query,user, PagerFilter.getUnlimitedFilter())
	*/
	
	
    counter = results.size() + 1
    log.debug(counter)
    
    def counterStr = String.format("%06d", counter)
    def zavBroj = counterStr + suffix

    zavodniBroj.updateValue(null,issue, new ModifiedValue(null, zavBroj),changeHolder)
    log.debug("zavBroj: " + zavBroj)

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
def za = issue.getCustomFieldValue(zavodniBroj)
issue.setSummary("Kandidat | $zavBroj | $ime $prezime | $broj") 
issueManager.updateIssue(user, issue, EventDispatchOption.ISSUE_UPDATED, false)
issueIndexingService.reIndex(issueManager.getIssueObject(issue.id));    
}


   