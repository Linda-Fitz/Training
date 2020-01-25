import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils

UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def parent = issue.getParentObject()
def subtasks = parent.getSubTaskObjects()

def brojCasovaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10912")
def brojCasova = parent.getCustomFieldValue(brojCasovaField).toInteger()

def brojZavrsenihCasova = 1

subtasks.each {
	if (it.getIssueTypeId() == "10601" && it.getStatusId() == "10313" && it != issue) {
		brojZavrsenihCasova += 1
	}
}

if (brojZavrsenihCasova >= brojCasova && parent.getStatusId() == "10100") {
 	workflowTransitionUtil.setIssue(parent);
	workflowTransitionUtil.setAction (31)    // > Zavrseno
    workflowTransitionUtil.validate();
    workflowTransitionUtil.progress();	
}