import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
// import org.apache.log4j.Logger
// import org.apache.log4j.Level
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.index.IssueIndexingService

// def log = Logger.getLogger("com.acme.Iteration")
// log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3839")
def changeHolder = new DefaultIssueChangeHolder();
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def datumField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_11408")
Date now = new Date(System.currentTimeMillis())
if(!issue.getCustomFieldValue(datumField)){
datumField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumField), now.toTimestamp()),changeHolder)
}