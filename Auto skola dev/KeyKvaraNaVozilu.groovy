import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.Datesssssss")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder();
def linkMgr = ComponentAccessor.getIssueLinkManager()
def cfManager = ComponentAccessor.getCustomFieldManager()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-29")

def keyKvaraField = cfManager.getCustomFieldObject("customfield_12399")

def parentIssue = issue.getParentObject()
log.debug(issue.getKey())

keyKvaraField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(keyKvaraField), issue.getKey()),changeHolder)
