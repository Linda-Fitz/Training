if (issue.getIssueTypeId() != '10200') { return }
if (issue.getStatusId() != '10100') { return }

import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KategorijaA")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def kategorijaField = cfManager.getCustomFieldObject("customfield_10203")
def kategorija = issue.getCustomFieldValue(kategorijaField)
def aKategorija = kategorija?.any { it.toString() == 'A' } 

if (!aKategorija) { return }

def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)
workflowTransitionUtil.setIssue(issue)
workflowTransitionUtil.setAction(731)
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()

