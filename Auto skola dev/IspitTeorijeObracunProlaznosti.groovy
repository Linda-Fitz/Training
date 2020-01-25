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

def log = Logger.getLogger("com.acme.ObracunProlaznosti")
log.setLevel(Level.DEBUG)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cf = ComponentAccessor.getCustomFieldManager()
WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setUserkey("visol")
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7217")

def poeni = cf.getCustomFieldObject("customfield_10825")
def prolaznost = cf.getCustomFieldObject("customfield_10817")
def linkMgr = ComponentAccessor.getIssueLinkManager()
def prijavljeniIspiti = []
def prijIspit

for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10402) {
    prijavljeniIspiti << link.getDestinationObject()    
  }
}

log.debug(prijavljeniIspiti)

prijavljeniIspiti.each{ ispit ->

   prijIspit = issueManager.getIssueByKeyIgnoreCase("$ispit")
   log.debug(prijIspit)
   def poeniTeoriju = prijIspit.getCustomFieldValue(poeni)
   log.debug(poeniTeoriju)
   
   if(poeniTeoriju == -1) {
      
      prolaznost.updateValue(null, prijIspit, new ModifiedValue(prijIspit.getCustomFieldValue(prolaznost), "DA"),changeHolder)
      log.debug(prijIspit.getCustomFieldValue(prolaznost))
      workflowTransitionUtil.setAction(51) // -> nije polozio
      workflowTransitionUtil.setIssue(prijIspit)
       
    } else if(poeniTeoriju == null){
	  
	  prolaznost.updateValue(null, prijIspit, new ModifiedValue(prijIspit.getCustomFieldValue(prolaznost), "NE"),changeHolder)
      log.debug(prijIspit.getCustomFieldValue(prolaznost))
      workflowTransitionUtil.setAction(51) // -> nije izasao
      workflowTransitionUtil.setIssue(prijIspit)
	  
	} else if(poeniTeoriju < 60) {    
        //61
      prolaznost.updateValue(null, prijIspit, new ModifiedValue(prijIspit.getCustomFieldValue(prolaznost), "NE"),changeHolder)
      log.debug(prijIspit.getCustomFieldValue(prolaznost))
      workflowTransitionUtil.setAction(61) // -> nije polozio
      workflowTransitionUtil.setIssue(prijIspit)

    } else if(poeniTeoriju < 71) {
       
      prolaznost.updateValue(null, prijIspit, new ModifiedValue(prijIspit.getCustomFieldValue(prolaznost), "DA"),changeHolder)
      log.debug(prijIspit.getCustomFieldValue(prolaznost))
      workflowTransitionUtil.setAction(41) // ->  polozio
      workflowTransitionUtil.setIssue(prijIspit)
	  
	} else if(poeniTeoriju == null){
	  
	  prolaznost.updateValue(null, prijIspit, new ModifiedValue(prijIspit.getCustomFieldValue(prolaznost), "NE"),changeHolder)
      log.debug(prijIspit.getCustomFieldValue(prolaznost))
      workflowTransitionUtil.setAction(51) // -> nije izasao
      workflowTransitionUtil.setIssue(prijIspit)
	}
  
  workflowTransitionUtil.validate()
  workflowTransitionUtil.progress()
} 

def grupeIspita = []
def grupaIssue

for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10501) {
    grupeIspita << link.getDestinationObject()    
  }
}
log.debug(grupeIspita)

grupeIspita.each{ grupa ->
  grupaIssue = issueManager.getIssueByKeyIgnoreCase("$grupa")
  log.debug(grupaIssue)
  workflowTransitionUtil.setAction(11) // ->  zavrsena
  workflowTransitionUtil.setIssue(grupaIssue)
  workflowTransitionUtil.validate()
  workflowTransitionUtil.progress()
}

