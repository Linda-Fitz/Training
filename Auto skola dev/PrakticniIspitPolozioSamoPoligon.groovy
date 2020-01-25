import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.util.JiraUtils

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ObracunProlaznosti")
log.setLevel(Level.DEBUG)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cf = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7392")

def poeni = cf.getCustomFieldObject("customfield_10825")
def prolaznost = cf.getCustomFieldObject("customfield_10817")
def linkMgr = ComponentAccessor.getIssueLinkManager()
def uplacenIspitPrakticni = cf.getCustomFieldObject("customfield_11263")

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 

def prijavljenIspit = []
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10701) {
    prijavljenIspit << link.getSourceObject()   
  }
}
log.debug(prijavljenIspit)
  
def brojNegativnihPoligonF = cf.getCustomFieldObject("customfield_11273")
def brojNegativnihGradskaF = cf.getCustomFieldObject("customfield_11274")

prijavljenIspit.each{ ispit ->
  def parent =(MutableIssue) ispit.getParentObject()
  log.debug(parent.summary)
    def id = ispit.getStatusId()
  log.debug(id)
  
  def negativniGradska = ispit.getCustomFieldValue(brojNegativnihGradskaF)
  def negativniPoligon = ispit.getCustomFieldValue(brojNegativnihPoligonF)

  log.debug(negativniGradska)
  //log.debug(negativniPoligon)

  if(negativniGradska >= 10 && id == "10605") {  
      log.debug("poligon")
	  workflowTransitionUtil.setAction(161) // ->  Nije polozio
      workflowTransitionUtil.setIssue(parent)
	  workflowTransitionUtil.validate();
	  workflowTransitionUtil.progress();
	  
	  def fieldConfig = uplacenIspitPrakticni.getRelevantConfig(parent)
	  def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
	  uplacenIspitPrakticni.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitPrakticni), ne ),changeHolder)
    } 
}

def i = issue
log.debug(i)

workflowTransitionUtil.setAction(31); // Zavrsi prakticni ispit
workflowTransitionUtil.setIssue(i);
workflowTransitionUtil.validate();
workflowTransitionUtil.progress();
