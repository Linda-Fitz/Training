import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.MutableIssue; 
def log = Logger.getLogger("com.acme.PolozioIspitTeorije")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3854")
def uplacenIspitTeorije = cfManager.getCustomFieldObject("customfield_10637")

def parent =(MutableIssue) issue.getParentObject()

if (parent.getStatusId() == "10203") { //spreman za prakticnu obuku
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(651) //nazad u teorija nije polozio
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
	
	def fieldConfig = uplacenIspitTeorije.getRelevantConfig(parent)
	def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
	uplacenIspitTeorije.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitTeorije), ne),changeHolder)
}
