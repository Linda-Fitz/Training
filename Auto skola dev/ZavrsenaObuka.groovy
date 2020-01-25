import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl

import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.CheckZavrsenaObuka")
log.setLevel(Level.DEBUG)

cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

Issue teorijaIssue
for (IssueLink link in linkMgr.getInwardLinks(issue.getParentObject().id)){
	if (link.getLinkTypeId() == 10500) {
		teorijaIssue = link.getSourceObject()
	}
}
if (!teorijaIssue) { return }

Boolean toTransition = true

for (IssueLink linkToGrupe in linkMgr.getOutwardLinks(teorijaIssue.id)){
	if (linkToGrupe.getLinkTypeId() == 10500) {
		def grupaIssue = linkToGrupe.getDestinationObject()

		Boolean toTransitionGrupa = true
		grupaIssue.getSubTaskObjects().each {
			if (it.getStatusId() != "10302") { 
				toTransition = false
				toTransitionGrupa = false
			}
		}

		if (toTransitionGrupa && grupaIssue.getStatusId() == "10311") {
			workflowTransitionUtil.setAction(71)   
			workflowTransitionUtil.setIssue(grupaIssue)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()			
		}
	}
}

if (toTransition && teorijaIssue.getStatusId() == "10311") {
	workflowTransitionUtil.setAction(81)   
	workflowTransitionUtil.setIssue(teorijaIssue)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
}