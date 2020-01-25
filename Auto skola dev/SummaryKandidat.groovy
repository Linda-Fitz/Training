import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-582")
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()

    def imeField = cfManager.getCustomFieldObject("customfield_10132")
	def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
	def ime = issue.getCustomFieldValue(imeField)
	if(!ime){
		ime = ""
	}
	def prezime = issue.getCustomFieldValue(prezimeField)
	if(!prezime){
		prezime = ""
	}
	def zavodniBrojField = cfManager.getCustomFieldObject("customfield_10321")
	def zavodniBroj = issue.getCustomFieldValue(zavodniBrojField)
if(!zavodniBroj){
		zavodniBroj = ""
	}
    def brojField = cfManager.getCustomFieldObject("customfield_10220")
	def broj = issue.getCustomFieldValue(brojField)
	if(!broj){
		broj = ""
	}

	//MutableIssue kandidat = issue.getIssue()
	issue.setSummary("Kandidat | $zavodniBroj | $ime $prezime | $broj") 