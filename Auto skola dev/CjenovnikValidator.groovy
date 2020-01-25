import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.CjenovnikValidator")
log.setLevel(Level.DEBUG)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("CE-312")

def vozilaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10810")
def vozila = issue.getCustomFieldValue(vozilaField)
if (!vozila) {vozila = ""}
def vozilastring = ""
if (vozila.size() > 0) {
	vozila.each{vozilo ->
		vozilastring += "\"" + vozilo.getKey() + "\","  
	}
	vozilastring = vozilastring.substring(0, vozilastring.size() - 1)
}

def vrstaPlacanjaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10320")
def vrstaPlacanja = issue.getCustomFieldValue(vrstaPlacanjaField).toString()

def query 
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
if (vozila.size() > 0) {
	query = "issuetype = Cjenovnik AND status in (aktivno) AND cf[10320] = \"$vrstaPlacanja\" AND cf[10810] in ($vozilastring)"
} else {
	query = "issuetype = Cjenovnik AND status in (aktivno) AND cf[10320] = \"$vrstaPlacanja\" AND cf[10810] is EMPTY"
}

queryIssue = shell.parse(new File("$basePath/general.groovy"))
counter = queryIssue.executeJQL(query)
counter = counter.size()


if (counter > 0) {
	//log.debug("erra")
	invalidInputException = new InvalidInputException("Ve\u0107 postoji aktivan cjenovnik za tu vrstu pla\u0107anja, odn. vozilo")	
}

//invalidInputException = new InvalidInputException("Kea $vozilastring")