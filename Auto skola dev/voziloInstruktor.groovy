import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.index.IssueIndexingService
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.user.ApplicationUser

def log = Logger.getLogger("com.acme.InstruktorVoziloLinkListener")
log.setLevel(Level.DEBUG)
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("CE-764")

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

if (issue.getIssueTypeId() != "10101") { 
	return 
}

def voziloField = cfManager.getCustomFieldObject("customfield_10324")
def vozilo = issue.getCustomFieldValue(voziloField)
def instruktoriField = cfManager.getCustomFieldObject("customfield_10403")
def instruktori = vozilo?.getCustomFieldValue(instruktoriField)

if (!instruktori) {
	instruktori = [issue]
	instruktoriField.updateValue(null, vozilo, new ModifiedValue(vozilo.getCustomFieldValue(instruktoriField), instruktori),new DefaultIssueChangeHolder())
} 
else if (!instruktori.contains(issue)) {
	instruktori.add(issue)
	instruktoriField.updateValue(null, vozilo, new ModifiedValue(vozilo.getCustomFieldValue(instruktoriField), instruktori),new DefaultIssueChangeHolder())
}

issueIndexingService.reIndex(vozilo)