if (issue.getIssueTypeId() != "10705") { return }

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.BrojPoenaPraksaListener")
log.setLevel(Level.DEBUG)

def negativniGradskaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find { it.field.contains("Broj negativnih poena") }
if (!negativniGradskaChanged) { return }

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def cf = ComponentAccessor.getCustomFieldManager()

def negativniGradskaField = cf.getCustomFieldObject("customfield_11274")
def negativniPoligonField = cf.getCustomFieldObject("customfield_11273")
def negativniGradska = issue.getCustomFieldValue(negativniGradskaField)
def negativniPoligon = issue.getCustomFieldValue(negativniPoligonField)

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setIssue(issue);
log.debug(negativniGradska)
log.debug(negativniPoligon)
if (negativniGradska == -1 || negativniPoligon == -1) {
	workflowTransitionUtil.setAction(51); // Nije izasao
} else if(negativniPoligon >= 8) {
	workflowTransitionUtil.setAction(61); // Nije polozio
} else if(negativniGradska >= 10) {    
	workflowTransitionUtil.setAction(91); // Polozio poligon
} else {
	workflowTransitionUtil.setAction(41); // Polozio
}

workflowTransitionUtil.validate();
workflowTransitionUtil.progress();