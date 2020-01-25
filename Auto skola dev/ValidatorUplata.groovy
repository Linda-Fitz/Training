import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import com.opensymphony.workflow.InvalidInputException

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def ukupnoZaPlatitiField = cfManager.getCustomFieldObject("customfield_11302")
def ukupnoUplateField = cfManager.getCustomFieldObject("customfield_10806")
def ukupnoZaPlatiti = issue.getCustomFieldValue(ukupnoZaPlatitiField)
def ukupnoUplate = issue.getCustomFieldValue(ukupnoUplateField)

log.debug("Ukupno za platiti" + ukupnoZaPlatiti)
log.debug("Ukupno uplate" + ukupnoUplate)


if (ukupnoUplate < ukupnoZaPlatiti){

	 invalidInputException = new InvalidInputException("Kandidat je duzan da plati ukupan dogovoreni iznos!") 
}