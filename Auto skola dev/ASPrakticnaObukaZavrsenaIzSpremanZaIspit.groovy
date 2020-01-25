import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZavrsiPrakticnuObuku")
log.setLevel(Level.DEBUG)

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setUserkey("visol")

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()

def prakticnaObukaLink = linkMgr.getOutwardLinks(issue.id).find { it.getLinkTypeId() == 10504 && it.getDestinationObject().getIssueTypeId() == "10304" }
def prakticnaObukaIssue = prakticnaObukaLink.getDestinationObject()


/*  Disableovana tranzicija privremeno, jer je bilo neophodno da
*	se zakaze ispit, a sama obuka jos uvijek nije zavrsena i casovi
*	ce se zakazivati nakon kreiranja ispita.
*	Dodat user Visol na trigger tranzicije u ovoj skripti
* Dodata skripta na Spreman za izdavanje svjedocanstva
*/


if (prakticnaObukaIssue.getStatusId() == "10300") { 
    workflowTransitionUtil.setIssue(prakticnaObukaIssue);
    workflowTransitionUtil.setAction(21) 
   workflowTransitionUtil.validate();
   workflowTransitionUtil.progress();    
}
