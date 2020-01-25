import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.PrijaviKandidate")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-448")
def ispitField = cfManager.getCustomFieldObject("customfield_10814")
def kandidatiField = cfManager.getCustomFieldObject("customfield_11219")
def kandidatiZaprijavu = issue.getCustomFieldValue(kandidatiField)

kandidatiZaprijavu.each { it ->
	def kandidat = issueManager.getIssueByKeyIgnoreCase(it.key)
	log.debug(kandidat)
	if (kandidat.getStatusId() != "10305" && kandidat.getStatusId() != "10304") { return }
	kandidat.setCustomFieldValue(ispitField, issue)

	// Tranzicija prijava
	workflowTransitionUtil.setAction (101) // prijava ispita na kandidatu
	workflowTransitionUtil.setIssue(kandidat)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()
}

issue.setCustomFieldValue(kandidatiField, null)