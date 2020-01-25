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
 
def log = Logger.getLogger("com.acme.ZavrsenCasTeorije")
log.setLevel(Level.DEBUG)

cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
changeHolder = new DefaultIssueChangeHolder()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def brOdslusaneTeorijeField = cfManager.getCustomFieldObject("customfield_10619")
for (IssueLink linkToKandidati in linkMgr.getOutwardLinks(issue.id)){
	if (linkToKandidati.getLinkTypeId() == 10403) {
		def kandidatIssue = linkToKandidati.getDestinationObject()
		def counter = 0
		for (IssueLink linkToGrupe in linkMgr.getInwardLinks(kandidatIssue.id)) {
			if (linkToGrupe.getLinkTypeId() == 10403) {
				counter += 1
			}
		}
		brOdslusaneTeorijeField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brOdslusaneTeorijeField), counter.toString()), changeHolder)
	}
}