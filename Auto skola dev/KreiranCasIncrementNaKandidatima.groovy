import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue

import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.IncrementBrojCasovaNaKandidatima")
log.setLevel(Level.DEBUG)

cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
changeHolder = new DefaultIssueChangeHolder()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def brCasovaTeorijeField = cfManager.getCustomFieldObject("customfield_10823")
def brObukaField = cfManager.getCustomFieldObject("customfield_10634")

for (IssueLink linkToKandidati in linkMgr.getOutwardLinks(issue.id)){
	if (linkToKandidati.getDestinationObject().getIssueTypeId() == "10200") {
		def kandidatIssue = linkToKandidati.getDestinationObject()
		def linkovi = linkMgr.getInwardLinks(kandidatIssue.id)
		def linkoviDoGrupa = linkMgr.getInwardLinks(kandidatIssue.id).findAll { it.getSourceObject().getIssueTypeId() == "10700" }
		def linkoviDoCasova = linkMgr.getInwardLinks(kandidatIssue.id).findAll { it.getSourceObject().getIssueTypeId() == "10401" }

		brCasovaTeorijeField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brCasovaTeorijeField), linkoviDoCasova.size().toDouble()), changeHolder)
		brObukaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brObukaField), linkoviDoGrupa.size().toDouble()), changeHolder)

		if (kandidatIssue.getStatusId() == "10100") { // AKTIVNO
			workflowTransitionUtil.setAction(41) // teorijska obuka
			workflowTransitionUtil.setIssue(kandidatIssue)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()			
		}
	}
}