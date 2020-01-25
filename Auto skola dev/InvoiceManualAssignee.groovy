import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.InvoiceManualAssignee")
log.setLevel(Level.DEBUG)

def issueService = ComponentAccessor.getIssueService()

ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
ApplicationUser visol = ComponentAccessor.getUserUtil().getUserByName("VISOL")

if (!issue.getAssignee()) {
	def validateAssignResult = issueService.validateAssign(visol, issue.id, user.name)
	issueService.assign(visol, validateAssignResult)	
}