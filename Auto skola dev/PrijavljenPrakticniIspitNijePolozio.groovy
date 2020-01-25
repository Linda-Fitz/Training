import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.MutableIssue; 
import com.atlassian.jira.issue.ModifiedValue

def log = Logger.getLogger("com.acme.PolozioPrakticniIspit2")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3854")
def datumIVrijemePolaganjaPoligonField = cfManager.getCustomFieldObject("customfield_11260")
def datumPolozenogPoligonField = cfManager.getCustomFieldObject("customfield_11267")
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_11266")
def uplacenIspitPrakticni = cfManager.getCustomFieldObject("customfield_11263")

def parent =(MutableIssue) issue.getParentObject()
//def ispit = parent.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11265"))

log.debug(parent.getCustomFieldValue(uplacenIspitPrakticni))

if (parent.getStatusId() == "10307") { //voznja ispit prijavljen

	fieldConfig = uplacenIspitPrakticni.getRelevantConfig(parent)
	def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
	uplacenIspitPrakticni.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitPrakticni), ne),changeHolder)
	
	log.debug(parent.getCustomFieldValue(uplacenIspitPrakticni))
	
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(161) //nazad u voznja nije polozio
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
}

//if (parent.getStatusId() == "10204") { //spreman za izdavanje svedocanstva

	def fieldConfig = uplacenIspitPrakticni.getRelevantConfig(parent)
	def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
	uplacenIspitPrakticni.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitPrakticni), ne),changeHolder)
	
	log.debug(parent.getCustomFieldValue(uplacenIspitPrakticni))
	/*
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(631) //nazad u voznja nije polozio
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	*/
	
	
//}

