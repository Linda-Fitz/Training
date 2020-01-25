import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.ModifiedValue
import java.sql.Timestamp
import java.util.Date
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.DelivereDebug")
log.setLevel(Level.DEBUG)
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-263")
def linkMgr = ComponentAccessor.getIssueLinkManager()

ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def prisutniField = cfManager.getCustomFieldObject("customfield_10618") 
def prisutni = issue.getCustomFieldValue(prisutniField)

prisutni.each{it ->
    if (it.getStatusId() == "10100") {
        WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
            workflowTransitionUtil.setIssue(it)
            workflowTransitionUtil.setUserkey("visol")
            workflowTransitionUtil.setAction (41)    
    
            workflowTransitionUtil.validate()
            workflowTransitionUtil.progress()

        }        
}
