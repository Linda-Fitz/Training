import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.PrijaviKandidate")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def linkMgr = ComponentAccessor.getIssueLinkManager()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-8010")
def ispitField = cfManager.getCustomFieldObject("customfield_11265")
def kandidatiZaprijavuField = cfManager.getCustomFieldObject("customfield_11270")
def kandidatiZaprijavu = issue.getCustomFieldValue(kandidatiZaprijavuField)

//kandidatiZaprijavuField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(kandidatiZaprijavuField), null), new DefaultIssueChangeHolder())

kandidatiZaprijavu.each { it ->
	Issue kandidati = issueManager.getIssueByKeyIgnoreCase(it.key)
log.debug(kandidati)
    kandidati.each{kandidat ->
	
	kandidat.setCustomFieldValue(ispitField, issue)
    linkMgr.createIssueLink(kandidat.id, issue.id, 10506, 1, visol)
	
	workflowTransitionUtil.setAction (151) // prijava ispita na kandidatu
	workflowTransitionUtil.setIssue(kandidat)	
	
    Issue prijavljenPrakticcni = kandidat.getSubTaskObjects().find{it.getIssueTypeId() == "10705" && it.getStatusId() == "10404"}
	log.debug(prijavljenPrakticcni)
	prijavljenPrakticcni.each{prakticni->
	linkMgr.createIssueLink(prakticni.id, issue.id, 10402, 1, visol)
	}
		workflowTransitionUtil.validate()
		workflowTransitionUtil.progress()
    }
}

if (kandidat.getStatusId() == "10307") { return }