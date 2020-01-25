import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import com.opensymphony.workflow.InvalidInputException

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-756")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

def knjigeField = cfManager.getCustomFieldObject("customfield_10414")
def knjige = issue.getCustomFieldValue(knjigeField).toString()
Boolean instruktor


if (knjige.toString().contains("NE") ) {

  invalidInputException = new InvalidInputException("Kandidat je duzan da vrati zaduzene knjige ili za iste uplati dogovoreni iznos!") 
}
