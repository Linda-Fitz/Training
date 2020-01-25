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

//def issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-3063")

def ispitIssue
for (IssueLink link in ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10501) { 
		ispitIssue = link.getSourceObject()
	}
}
def subtasks = []
def counter = 0

if (!ispitIssue) { return }

Boolean toTransition = true
for (IssueLink link in ComponentAccessor.getIssueLinkManager().getOutwardLinks(ispitIssue.id)) {
	if (link.getLinkTypeId() == 10501 && link.getDestinationObject().getStatusId() != "10313") { // Grupa u statusu završeno
		toTransition == false
		def grupaIssue = link.getDestinationObject()
		subtasks <<  grupaIssue
		counter = counter+1
	}
}
log.debug(subtasks)
log.debug(counter)
log.debug(toTransition)
log.debug(ispitIssue.getStatusId())

if (!toTransition || ispitIssue.getStatusId() != "10312") { return }

if (counter <= 1){
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
workflowTransitionUtil.setAction(31)  // zavrsi ispit
workflowTransitionUtil.setIssue(ispitIssue)
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()
}