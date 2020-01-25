import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.ZavrsenaGrupaIspita")
log.setLevel(Level.DEBUG)

//def issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-3039")

def ispitIssue
for (IssueLink link in ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10501) { 
		ispitIssue = link.getSourceObject()
	}
}
if (!ispitIssue || ispitIssue.getStatusId() != "10400") { return } //grupe kreirane, sve spremno

WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction(41)  // zapocni ispit
workflowTransitionUtil.setIssue(ispitIssue)
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()