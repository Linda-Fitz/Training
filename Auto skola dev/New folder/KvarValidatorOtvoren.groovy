import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.opensymphony.workflow.WorkflowContext;
import com.atlassian.jira.config.SubTaskManager
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level

def issueManager = ComponentAccessor.getIssueManager() 
def log = Logger.getLogger("com.acme.PRLListener")
log.setLevel(Level.DEBUG)
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-441")
Issue parentIssue = issue.getParentObject()
//def man = parentIssue.getKey()
//return man
SubTaskManager subTaskManager = ComponentAccessor.getSubTaskManager();
 def subtasks = parentIssue.getSubTaskObjects();
 
  //if(subtasks){
       
    def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
    def searchProvider = ComponentAccessor.getComponent(SearchProvider)
    def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    GroovyShell shell = new GroovyShell()
    def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
    String query = "issuetype = Kvar  AND status = 10102"
    def queryIssue = shell.parse(new File("$basePath/general.groovy"))
    def counter = queryIssue.executeJQL(query)

    log.debug("Issue: " + issue.getKey())
    log.debug("Parent: " + parentIssue.getKey())

    counter.each{it->
           
      if(it.getStatusId() == "10102" && it.getParentObject() == parentIssue) {
        log.debug(it.getKey() + " " + it.getParentObject().getKey())
           
        invalidInputException = new InvalidInputException("Ve\u0107 postoji kvar koji je otvoren")   
      }        
    }