import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.event.type.EventDispatchOption
import java.sql.Timestamp
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.IssueReindex")
log.setLevel(Level.DEBUG)

def userUtil = ComponentAccessor.getUserUtil()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")

def issueManager = ComponentAccessor.getIssueManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
def changeHolder = new DefaultIssueChangeHolder();
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("SIF-1207")

issueIndexingService.reIndex(issue)