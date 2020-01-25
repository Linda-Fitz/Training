if (issue.getIssueTypeId() != "10200") {return}
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.GrupaIspitalistener")
log.setLevel(Level.DEBUG)

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()

 
def grupaIspita = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field == "Grupa"}
log.debug(grupaIspita)
if(!grupaIspita){return}
def novoGrupa = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11221"))
linkMgr.createIssueLink(issue.id, novoGrupa.id, 10502, 1, visol)

staraGrupa = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(grupaIspita.oldstring)	
def grupaLink = linkMgr.getOutwardLinks(issue.id).find { it.getLinkTypeId() == 10502 && it.getDestinationObject().getIssueTypeId() == "10701" && it.getDestinationObject() == staraGrupa}
    log.debug(staraGrupa)
    linkMgr.removeIssueLink(grupaLink, visol)


	
	
