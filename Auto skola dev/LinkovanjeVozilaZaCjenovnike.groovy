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

import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.LinkovanjeVozilaZaCjenovnike")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-448")

def voziloField = cfManager.getCustomFieldObject("customfield_12505")
def voziloZaLinkovanje = issue.getCustomFieldValue(voziloField)

voziloZaLinkovanje.each { it ->
	def vozilo = issueManager.getIssueByKeyIgnoreCase(it.key)
	log.debug(vozilo)
	linkMgr.createIssueLink(issue.id, vozilo.id, 10400, 1, currentUser)

}