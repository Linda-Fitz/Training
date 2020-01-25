import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Level
import org.apache.log4j.Logger

log = Logger.getLogger("com.acme.CreateUser")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def userUtil = ComponentAccessor.getUserUtil()
def visol = userUtil.getUserByName("VISOL")
UserService userService = ComponentAccessor.getComponent(UserService.class)

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3605")
//MutableIssue issue = issue

def instruktorField = cfManager.getCustomFieldObject("customfield_10913")
def userZaposleniField = cfManager.getCustomFieldObject("customfield_10207")
def instruktor = issue.getCustomFieldValue(instruktorField)
def user = instruktor.getCustomFieldValue(userZaposleniField)
	issue.setAssignee(user)
	//issue.setCustomFieldValue(usernameField, username)
	//issue.setCustomFieldValue(userField, createdUser)
	issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)
    