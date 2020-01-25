import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue

import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KandidatBrojCasovaListener")
log.setLevel(Level.DEBUG)

cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()

if (issue.getIssueTypeId() != "10401") { 
	return 
}

for (IssueLink link in linkMgr.getOutwardLinks(issue.id)){
	log.debug(link.getDestinationObject().getKey())
	if (link.getLinkTypeId() == 10403) {
		countCasoveObuke(link.getDestinationObject())
	}
}

def countCasoveObuke(Issue kandidat) {
	def brojCasovaField = cfManager.getCustomFieldObject("customfield_10823")
	double brojCasova = 0
	for (IssueLink link in linkMgr.getInwardLinks(kandidat.id)){
		if (link.getLinkTypeId() == 10403) {
			brojCasova += 1
		}
	}
	brojCasovaField.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(brojCasovaField), brojCasova),new DefaultIssueChangeHolder())
}