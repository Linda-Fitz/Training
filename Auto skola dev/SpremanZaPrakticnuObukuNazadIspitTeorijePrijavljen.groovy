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

def log = Logger.getLogger("com.acme.NijePolozioIspitTeorije")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3854")
def parent =(MutableIssue) issue.getParentObject()

if (parent.getStatusId() == "10203") { //spreman za prakticnu obuku
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(671) //nazad u ispit prijavljen
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
	
	def fieldConfig = uplacenIspitTeorije.getRelevantConfig(parent)
	def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'DA'}
	uplacenIspitTeorije.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitTeorije), da),changeHolder)
}
