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
def log = Logger.getLogger("com.acme.PolozioPrakticniIspit")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3854")
def datumIVrijemePolaganjaPoligonField = cfManager.getCustomFieldObject("customfield_11260")
def datumPolozenogPoligonField = cfManager.getCustomFieldObject("customfield_11267")
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_11266")

def parent =(MutableIssue) issue.getParentObject()
def ispit = parent.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11265"))

def brojPolaganja = issue.getCustomFieldValue(brojPolaganjaField)
def datumIVrijemePolaganjaPoligon = ispit.getCustomFieldValue(datumIVrijemePolaganjaPoligonField)

datumPolozenogPoligonField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPolozenogPoligonField), datumIVrijemePolaganjaPoligon), changeHolder)
datumPolozenogPoligonField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(datumPolozenogPoligonField), datumIVrijemePolaganjaPoligon), changeHolder)
log.debug(datumIVrijemePolaganjaPoligon)
brojPolaganjaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojPolaganjaField), brojPolaganja), changeHolder)

if (parent.getStatusId() == "10204") { //spreman za svedocanstvo
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(621) // voznja ispit prijavljen
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
}