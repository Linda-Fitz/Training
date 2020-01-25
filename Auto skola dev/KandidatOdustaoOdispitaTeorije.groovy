import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

def issueManager = ComponentAccessor.getIssueManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-370")
 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def parentIssue = (MutableIssue)issue.getParentObject() 
def subtasks = issue.getSubTaskObjects()
def linkMgr = ComponentAccessor.getIssueLinkManager()

if (parentIssue) 
{   
    def currentParentStatus = parentIssue.getStatusId()
    if (currentParentStatus == "10303") 
    {  
        WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
        workflowTransitionUtil.setUserkey("visol")
        workflowTransitionUtil.setAction(771);
        workflowTransitionUtil.setIssue(parentIssue);          
        workflowTransitionUtil.validate();
        workflowTransitionUtil.progress();
    }        
}

def issuesIspit = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id).find { it.getLinkTypeId() == 10402 }
def issuesIspitTeorije = issuesIspit.getSourceObject()
def brojPrijavljenihField = cfManager.getCustomFieldObject("customfield_11262")
brojPrijavljenih = issuesIspitTeorije.getCustomFieldValue(brojPrijavljenihField)
brojPrijavljenih = brojPrijavljenih - 1
brojPrijavljenihField.updateValue(null, issuesIspitTeorije, new ModifiedValue(issue.getCustomFieldValue(brojPrijavljenihField), brojPrijavljenih),changeHolder)
linkMgr.removeIssueLink(issuesIspit, visol)
def issuesGrupa = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issuesIspitTeorije.id).find{ it.getLinkTypeId() == 10501 }
def issuesGrupaT = issuesGrupa.getDestinationObject()
/*
issuesGrupaT.each{it->
    def issuesPKandidat = ComponentAccessor.getIssueLinkManager().getInwardLinks(it.id).find{ it.getLinkTypeId() == 10600 }
    linkMgr.removeIssueLink(issuesPKandidat, visol)
}
*/