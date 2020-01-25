import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.TeorijskaObukaValidator")
log.setLevel(Level.DEBUG)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = \"Teorijska obuka\" AND statusCategory != \"Done\""
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def results = queryIssue.executeJQL(query)
counter = results.size()

if (counter > 0) 
{
    invalidInputException = new InvalidInputException("Ve\u0107 postoji teorijska obuka u toku")    
}