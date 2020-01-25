import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def issueManager = ComponentAccessor.getIssueManager()

issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)