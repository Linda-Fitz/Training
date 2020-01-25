import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.bc.user.UserService
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KondicioniAssigneeListener")
log.setLevel(Level.DEBUG)

if (issue.getIssueTypeId() != "10600") { return } // Kondicioni kandidat

MutableIssue issue = issue
def instruktorDodijeljen = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field == "Dodijeljeni instruktor"}

if (instruktorDodijeljen) {
	ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
	def instruktorIssue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(instruktorDodijeljen.newstring)
	issue.setAssignee(instruktorIssue.getAssignee())
	ComponentAccessor.getIssueManager().updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)
}	