import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.PromenaStatusaKupona")
log.setLevel(Level.DEBUG)

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-210")
def datumPoligonaField = cfManager.getCustomFieldObject("customfield_11260")
def prakticniIspitField = cfManager.getCustomFieldObject("customfield_11265")
def datumField = cfManager.getCustomFieldObject("customfield_12308")
def prakticniIspit = issue.getCustomFieldValue(prakticniIspitField)

Issue ispitIssue = issueManager.getIssueByKeyIgnoreCase(prakticniIspit.key)
log.debug(ispitIssue)

def datumPoligona = ispitIssue.getCustomFieldValue(datumPoligonaField)

datumField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPoligonaField), datumPoligona),changeHolder)
