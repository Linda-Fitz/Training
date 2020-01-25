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
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("CE-446")
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()

    def imeField = cfManager.getCustomFieldObject("customfield_10132")
	def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
	def ime = issue.getCustomFieldValue(imeField)
	def prezime = issue.getCustomFieldValue(prezimeField)
	def brUgovoraField = cfManager.getCustomFieldObject("customfield_10217")
	def brUgovora = issue.getCustomFieldValue(brUgovoraField)
//return brUgovora  

//String selectedIssueType = getIssueContext().getIssueType().getName();
def summary = "Ugovor o radu | $ime $prezime | $brUgovora"
//return summary

	MutableIssue ugovor = issueFactory.getIssue()
	issue.setSummary(summary) 

