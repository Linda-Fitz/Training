import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
def changeHolder = new DefaultIssueChangeHolder();
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()

double cijena = 0.000000000000000000d

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-1257")

def	kilometrazaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10311") 
def	iznosField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10316") 
def	litaraField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10317") 
def	cijenaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10516")

def iznos = (issue.getCustomFieldValue(iznosField)) ? issue.getCustomFieldValue(iznosField) : "0"
def litara = (issue.getCustomFieldValue(litaraField)) ? issue.getCustomFieldValue(litaraField) :"0"

cijena = iznos / litara

cijenaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cijenaField), cijena),changeHolder)

