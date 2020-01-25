import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.JiraUtils
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.TeorijaSve")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-3332")

ispitField = cfManager.getCustomFieldObject("customfield_11265")

def kandidati = []
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10506 || link.getLinkTypeId() == 10505) {
		link.getSourceObject().getSubTaskObjects().each { subtask ->
			log.debug(subtask.getCustomFieldValue(ispitField))
			log.debug(subtask.getStatusId())
			if (subtask.getCustomFieldValue(ispitField) == issue && subtask.getStatusId() == "10406") {
				workflowTransitionUtil.setAction (71) // ispit -> na spisku
				workflowTransitionUtil.setIssue(subtask)
				workflowTransitionUtil.validate()
				workflowTransitionUtil.progress()			
			}
		}	
	} else if (link.getLinkTypeId() == 10507) {
		// Nazad u status koji je bio
		def kandidatNijeUplatioIssue = link.getSourceObject()
		if (kandidatNijeUplatioIssue.getStatusId() == "10307") {
			Boolean padaoIspit
			def prijavljeniIspiti = kandidatNijeUplatioIssue.getSubTaskObjects().findAll{ it.getIssueTypeId() == "10705" && it.getStatusId() == "10409"}
			prijavljeniIspiti.each {
				if (it.getStatusId() == "10409") { padaoIspit = true }
			}
			if (padaoIspit) {
				workflowTransitionUtil.setAction(161)			
			} /*else {
				//workflowTransitionUtil.setAction(71)			
			}*/
			workflowTransitionUtil.setIssue(kandidatNijeUplatioIssue)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()				
		}
		//Subtask status u odustao
		def subtask = kandidatNijeUplatioIssue.getSubTaskObjects().findAll { it.getCustomFieldValue(ispitField) == issue }
		/*
		subtask?.each {
			workflowTransitionUtil.setAction(81)
			workflowTransitionUtil.setIssue(it)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()				
		}
*/
		//Brisanje linka
		linkMgr.removeIssueLink(link, visol)
	}
}

// Broj prijavljenih 
def brojPrijavljenihField = cfManager.getCustomFieldObject("customfield_11262")
def prijavljeniLinks = linkMgr.getInwardLinks(issue.id).findAll { it.getLinkTypeId() == 10701 }
def brojPrijavljenih =  prijavljeniLinks.size()
brojPrijavljenihField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPrijavljenihField), (double)brojPrijavljenih), new DefaultIssueChangeHolder())