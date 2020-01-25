import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.TeorijaZapocni")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-389")

def obavjestenjeField = cfManager.getCustomFieldObject("customfield_10610")
def obavjestenje = issue.getCustomFieldValue(obavjestenjeField)

workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction(11)   
linkMgr = ComponentAccessor.getIssueLinkManager()

for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
    def linkedIssue = link.getDestinationObject()
    if (linkedIssue.getIssueTypeId() == "10700" && linkedIssue.getStatusId() == "10311") {
        workflowTransitionUtil.setIssue(linkedIssue)
        workflowTransitionUtil.validate()
        workflowTransitionUtil.progress()
    }
}