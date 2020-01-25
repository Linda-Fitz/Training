import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KreiranaGrupaTeorija")
log.setLevel(Level.DEBUG)

def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def linkMgr = ComponentAccessor.getIssueLinkManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-5986")
issue.setAssignee(currentUser)
issue.setReporter(currentUser)

def kandidati = []
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10502) {
		kandidati << link.getSourceObject()	
		link.getSourceObject().getSubTaskObjects().findAll{ it.getIssueTypeId() == "10405"}.each { subtask ->
		if(subtask.getStatusId()!= "10410") {return}
           linkMgr.createIssueLink(subtask.id, issue.id, 10600, 1, visol)	
           def ispitLink = linkMgr.getInwardLinks(issue.id).find{it.getLinkTypeId() == 10501}
		   def ispitIssue = ispitLink.getSourceObject()
           //linkMgr.createIssueLink(ispitIssue.id, subtask.id, 10402, 1, visol)
		}	
	}

}


