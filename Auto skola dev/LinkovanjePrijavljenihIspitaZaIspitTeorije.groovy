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
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-2277")

def linkToKandidat = linkMgr.getInwardLinks(issue.id).findAll{ it.getLinkTypeId() in ([10401, 10301,10502] as Long[] ) }
linkToKandidat.each{link->
kandidatIssue = link.getSourceObject()

kandidatIssue.each{it->
subKandidat = it.getSubTaskObjects().findAll{ it.getIssueTypeId() == "10405" }
subKandidat.each{sub->
if(sub.getStatusId() != "10410") {return}
log.debug(sub)
linkMgr.createIssueLink(issue.id, sub.id, 10402, 1, visol)    
    }

}
 
}

def brojPrijavljenihField = cfManager.getCustomFieldObject("customfield_11262")
//def brojPrijavljenih = linkMgr.getInwardLinks(issue.id).size()
def prijavljeni = linkMgr.getInwardLinks(issue.id).findAll{ it.getLinkTypeId() == 10402 }
//def brojPrijavljenih = prijavljeni.size() + 1
/*brojPrijavljenihField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPrijavljenihField), (double)brojPrijavljenih), new DefaultIssueChangeHolder())
*/