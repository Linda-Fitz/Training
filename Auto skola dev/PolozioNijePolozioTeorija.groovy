if (issue.getIssueTypeId() != "10405") { return }

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;

def poeniChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field == "Broj poena"}
if (!poeniChanged) { return }

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() 
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-2188")
def cf = ComponentAccessor.getCustomFieldManager()
def poeni = cf.getCustomFieldObject("customfield_10825")
def prolaznost = cf.getCustomFieldObject("customfield_10817")
def poeniTeoriju = issue.getCustomFieldValue(poeni).toDouble()

UserUtil userUtil = ComponentAccessor.getUserUtil()

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setIssue(issue);
String DA
String NE
if(poeniTeoriju == -1) {
	workflowTransitionUtil.setAction(51);
	 prolaznost.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prolaznost), "DA"),changeHolder)
} else if(poeniTeoriju < 60) {    
	workflowTransitionUtil.setAction(61);
	prolaznost.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prolaznost), "NE"),changeHolder)
} else if(poeniTeoriju < 71) {
	workflowTransitionUtil.setAction(41);
	prolaznost.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prolaznost), "DA"),changeHolder)
}

workflowTransitionUtil.validate();
workflowTransitionUtil.progress();