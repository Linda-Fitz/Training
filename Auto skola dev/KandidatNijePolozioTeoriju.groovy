import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level


def log = Logger.getLogger("com.acme.NijePolozioTeoriju")
log.setLevel(Level.DEBUG)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cf = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7333")

def poeni = cf.getCustomFieldObject("customfield_10825")
def prolaznost = cf.getCustomFieldObject("customfield_10817")
def linkMgr = ComponentAccessor.getIssueLinkManager()

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setIssue(issue);

def prijavljeniIspiti = []
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10402) {
    prijavljeniIspiti << link.getDestinationObject()    
  }
}
log.debug(prijavljeniIspiti)
def prijIspit
def parent
prijavljeniIspiti.each{ ispit ->
   prijIspit = issueManager.getIssueByKeyIgnoreCase("$ispit")
   log.debug(prijIspit)

   parent =(MutableIssue) prijIspit.getParentObject()
	log.debug(parent.summary)
    log.debug(prijIspit.getStatusId())

   if (prijIspit.getStatusId() == "10408" || prijIspit.getStatusId() == "10409"){ //ispit teorije nije izasao i ispit teorije nije polozio
   		
       workflowTransitionUtil.setAction(111) // ->  Nije polozio
    	workflowTransitionUtil.setIssue(parent)
    	workflowTransitionUtil.validate()
    	workflowTransitionUtil.progress()
       
   }
   		
}
