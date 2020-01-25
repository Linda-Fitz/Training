import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.VoziloCreate")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueService = ComponentAccessor.getIssueService()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

issue.setAssignee(currentUser)
issue.setReporter(currentUser)
