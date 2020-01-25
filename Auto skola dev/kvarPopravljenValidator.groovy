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

def iznosField = cfManager.getCustomFieldObject("customfield_10316")
def iznos = issue.getCustomFieldValue(iznosField)

def opisField = cfManager.getCustomFieldObject("customfield_10400")
def opis = issue.getCustomFieldValue(opisField)

def vrstaField = cfManager.getCustomFieldObject("customfield_10326")
def vrsta = issue.getCustomFieldValue(vrstaField)

if (!iznos || !opis || !vrsta ) {
  invalidInputException = new InvalidInputException("Popunite polja!") 
}
