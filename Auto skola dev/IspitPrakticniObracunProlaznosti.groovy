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
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ObracunProlaznostiPrakticni")
log.setLevel(Level.DEBUG)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cf = ComponentAccessor.getCustomFieldManager()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7246")

def prolaznost = cf.getCustomFieldObject("customfield_10817")
def linkMgr = ComponentAccessor.getIssueLinkManager()

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setUserkey("visol")

def kandidati = []
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
  if (link.getLinkTypeId() == 10701) {
    kandidati << link.getSourceObject()   
  }
}
log.debug("kandidat: " + kandidati)
  
def brojNegativnihPoligonF = cf.getCustomFieldObject("customfield_11273")
def brojNegativnihGradskaF = cf.getCustomFieldObject("customfield_11274")
def brojNegativnihUkupnoF = cf.getCustomFieldObject("customfield_11506")
def prolaznostF = cf.getCustomFieldObject("customfield_10817")

kandidati.each{ kandidat ->
  def negativniGradska = kandidat.getCustomFieldValue(brojNegativnihGradskaF)
  def negativniPoligon = kandidat.getCustomFieldValue(brojNegativnihPoligonF)
   negativniGradska = negativniGradska ?: 0 
   negativniPoligon = negativniPoligon ?: 0 
 
  def brojNegativnih = (negativniGradska + negativniPoligon).toDouble()
  parentKandidat = kandidat.getParentObject()
 
  log.debug("poligon: " + negativniPoligon)
  log.debug("gradska: " + negativniGradska)
  log.debug("ukupno: " + brojNegativnih)
  
    if (negativniGradska == -1 || negativniPoligon == -1) {
      workflowTransitionUtil.setAction(51); // Nije izasao
      workflowTransitionUtil.setIssue(kandidat);
      prolaznostF.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(prolaznostF), "NIJE IZA\u0160AO/LA") ,changeHolder)
    } else if(negativniPoligon >= 8) {
      workflowTransitionUtil.setAction(61); // Nije polozio
      workflowTransitionUtil.setIssue(kandidat);
      prolaznostF.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(prolaznostF), "NIJE POLO\u017DIO/LA") ,changeHolder)
    } else if(negativniGradska >= 10) {    
      workflowTransitionUtil.setAction(91); // Polozio poligon
      workflowTransitionUtil.setIssue(kandidat);
      prolaznostF.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(prolaznostF), "POLO\u017DEN POLIGON") ,changeHolder)
    } else {
      workflowTransitionUtil.setAction(41); // Polozio
      workflowTransitionUtil.setIssue(kandidat);
      prolaznostF.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(prolaznostF), "POLO\u017DIO/LA") ,changeHolder)
	  linkMgr.createIssueLink(parentKandidat.id, issue.id, 10800, 1, visol)
    }

workflowTransitionUtil.validate();
workflowTransitionUtil.progress();

brojNegativnihUkupnoF.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(brojNegativnihUkupnoF), brojNegativnih), changeHolder)
}
