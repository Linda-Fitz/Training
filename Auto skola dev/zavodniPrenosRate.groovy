import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

 
def log = Logger.getLogger("com.acme.ZavodniPrenosRate")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByKey("VISOL")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-138")

def parent = issue.getParentObject()
log.debug("parent: "+ parent)
def zavodniBrojField = cfManager.getCustomFieldObject("customfield_10321")
def zavodniBroj = parent.getCustomFieldValue(zavodniBrojField)
log.debug("zavodniBroj: " + zavodniBroj)

zavodniBrojField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zavodniBrojField), zavodniBroj), changeHolder)
