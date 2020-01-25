import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZakaziCasVoznjeValidator")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()

def prakticnaObukaIssue
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)){
  	if (link.getLinkTypeId() == 10504 && link.getDestinationObject().getIssueTypeId() == "10304") {
  		prakticnaObukaIssue = link.getDestinationObject()
  		break
  	}
}

if (!prakticnaObukaIssue) {
	return
}

if (prakticnaObukaIssue.getSubTaskObjects()?.findAll{it.getStatusId() == "10501"}.size() > 0) {
	invalidInputException = new InvalidInputException("Ve\u0107 postoji zakazan \u010das vo\u017enje.")
}