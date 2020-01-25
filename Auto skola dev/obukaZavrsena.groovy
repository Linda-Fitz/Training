import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import org.apache.log4j.Level
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.ObukaZavrsena")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-389")
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction (21)   
linkMgr = ComponentAccessor.getIssueLinkManager()

for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
    def linkedIssue = link.getDestinationObject()
    if (linkedIssue.getIssueTypeId() == "10700" && linkedIssue.getStatusId() == "10300") {
        workflowTransitionUtil.setIssue(linkedIssue)
        workflowTransitionUtil.validate()
        workflowTransitionUtil.progress()
    }
}