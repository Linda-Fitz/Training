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

def log = Logger.getLogger("com.acme.ZavrsenaPrakticnaObuka")
log.setLevel(Level.DEBUG)

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7430")

def linkMgr = ComponentAccessor.getIssueLinkManager()
def kandidat 

for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10504) {
    kandidat = link.getSourceObject()    
  }
}
log.debug(kandidat)

def kand = issueManager.getIssueByKeyIgnoreCase(kandidat.toString())
log.debug(kand)
/*
workflowTransitionUtil.setAction(131) // -> Zavrsi redovnu obuku
workflowTransitionUtil.setIssue(kand)
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()
*/