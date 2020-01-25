import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.customfields.option.*
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.issue.IssueEvent
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.assignee")
log.setLevel(Level.DEBUG)
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder();
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
//Issue issue = issueManager.getIssueByKeyIgnoreCase("PT-45")
//def assignee = issue.getAssignee()

def lastField = cfManager.getCustomFieldObject("customfield_11510")
log.debug(event?.getChangeLog()?.getRelated("ChildChangeItem"))
def assigneePromijenjen = event?.getChangeLog()?.getRelated("ChildChangeItem")?.find {it.field == "assignee"}
if (assigneePromijenjen) {
    def oldValue = assigneePromijenjen.oldvalue
    def userUtil = ComponentAccessor.getUserUtil()
    def lastAssignee = userUtil.getUserByName(oldValue)
    log.debug(oldValue)
	lastField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(lastField), lastAssignee),changeHolder)
}
//issueManager.updateIssue(null, issue, EventDispatchOption.ISSUE_UPDATED, false)
