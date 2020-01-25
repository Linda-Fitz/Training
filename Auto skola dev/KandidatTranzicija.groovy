if (issue.getIssueTypeId() != '10200') { return }
if (issue.getStatusId() != '10100') { return }

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;

def issueManager = ComponentAccessor.getIssueManager()
def cf = ComponentAccessor.getCustomFieldManager()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByKey("VISOL")
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-919")

def pohadja = cf.getCustomFieldObject("customfield_10636")
def pohadjaTeoriju = issue.getCustomFieldValue(pohadja).toString()
if (!pohadjaTeoriju) { return }
WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
workflowTransitionUtil.setUserkey("visol")

if(pohadjaTeoriju != "DA") {   
    workflowTransitionUtil.setUserkey("visol")   
    workflowTransitionUtil.setIssue(issue);
    workflowTransitionUtil.setAction(121);          
    workflowTransitionUtil.validate();
    workflowTransitionUtil.progress();
} 
