import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.KandidatListener")
log.setLevel(Level.DEBUG)

if (issue.getIssueTypeId() != "10200") { return } // Kandidat

def uplacenIspit = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.contains("en ispit teorije") }

def linkId

if (uplacenIspit?.newstring == "DA") {
	linkId = 10301
} else if (uplacenIspit?.newstring == "DOZVOLJENO")	{
	linkId = 10401
} else if (uplacenIspit?.newstring == "NE") {
	linkId = 10300
}

def linkMgr = ComponentAccessor.getIssueLinkManager()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")

def linkToIspit = linkMgr.getOutwardLinks(issue.id).find { it.getDestinationObject().getIssueTypeId() == "10402" }
def ispitIssue = linkToIspit.getDestinationObject()

linkMgr.removeIssueLink(linkToIspit, visol)
linkMgr.createIssueLink(issue.id, ispitIssue.id, linkId, 1, visol)