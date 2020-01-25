import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.PromenaStatusaKupona")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-4272")

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def kuponField = cfManager.getCustomFieldObject("customfield_11503")
def kuponPolje = issue.getCustomFieldValue(kuponField)
if(!kuponPolje){return}

Issue kupon = issueManager.getIssueByKeyIgnoreCase(kuponPolje.key)

//log.debug(kupon)
def sum = kupon.summary
sum = sum.toString()
//log.debug("Summary: "+ sum)

def query 
def results
def rissue

if(kuponPolje){
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String queryActivate = "issuetype = Kupon AND status = AKTIVAN AND summary ~ \"$sum\""
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issuesToChange = queryIssue.executeJQL(queryActivate)
rissue = issueManager.getIssueByKeyIgnoreCase(issuesToChange.getIssues()[0].getKey())
    
    workflowTransitionUtil.setAction (11) // status = iskoriscen
 	workflowTransitionUtil.setIssue(rissue);
  	workflowTransitionUtil.validate();
  	workflowTransitionUtil.progress();
}


  