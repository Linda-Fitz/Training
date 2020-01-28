import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.web.action.issue.DeleteIssue
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.UserPropertyManager

import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.LinkovanjeVozilaZaCjenovnike")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")

Issue issue = issueManager.getIssueByKeyIgnoreCase("ce-173")
def  userPropertyManager = ComponentAccessor.getUserPropertyManager()
propValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.TEL')
propValue1 = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.TIS')

def bindTemplate(Issue issue) {
	def binding = [
		issueid: issue.getId().toString(),
		projectname: issue.getProjectObject().getName(),
		issuekey: issue.getKey(),
		summary: summary,
		description: description,
		datum: nowFormatted,
		datumObuke: datumObuke,
		vrijemeObuke: vrijemeObuke,
		adresaObuke: adresaObuke
	]

	return template.make(binding)	
}

