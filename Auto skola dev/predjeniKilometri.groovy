import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.test")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-644")
Issue parentIssue = issue.getParentObject();
def subtasks = parentIssue.getSubTaskObjects()
def predjenoKilometaraField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10513")

def datumField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10315")
def datum = issue.getCustomFieldValue(datumField)
def kilometrazaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10311")
def kilometraza = issue.getCustomFieldValue(kilometrazaField)
def utocenoField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10317")
def utoceno =  issue.getCustomFieldValue(utocenoField)

def prosjecnoField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10524")
def olderSubTasks = []
def newerSubTasks = []
subtasks.each {
    if (issue.getIssueTypeId() == it.getIssueTypeId() && it != issue) {
    	if (it.getCustomFieldValue(datumField) < issue.getCustomFieldValue(datumField)) {
    		olderSubTasks.add(it)
    	} else {
    		newerSubTasks.add(it)
    	}
    }
}

olderSubTasks.sort{a, b ->
    a.getCustomFieldValue(kilometrazaField) <=> b.getCustomFieldValue(kilometrazaField)
    a.getCustomFieldValue(datumField) <=> b.getCustomFieldValue(datumField)  
}
def predjeno = olderSubTasks.last().getCustomFieldValue(kilometrazaField)
def razlika = kilometraza - predjeno
log.debug("issue: " + (kilometraza).toString())
log.debug("issue: " + (predjeno).toString())
log.debug("issue: " + (kilometraza - predjeno).toString())

predjenoKilometaraField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(predjenoKilometaraField), razlika),changeHolder) 
prosjecnoField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(predjenoKilometaraField),  utoceno / razlika * 100),changeHolder) 

newerSubTasks.sort{a, b ->
    a.getCustomFieldValue(kilometrazaField) <=> b.getCustomFieldValue(kilometrazaField)
    a.getCustomFieldValue(datumField) <=> b.getCustomFieldValue(datumField)  
}

newerSubTasks.each {
	log.debug(it)
	predjenoKilometara = it.getCustomFieldValue(kilometrazaField) + razlika
	utoceno = it.getCustomFieldValue(utocenoField)

	log.debug("Task: " + it.key + " " + predjenoKilometara.toString())
	log.debug("Task: " + it.key + " " + utoceno.toString())
	predjenoKilometaraField.updateValue(null, it, new ModifiedValue(it.getCustomFieldValue(predjenoKilometaraField), razlika),changeHolder) 
	prosjecnoField.updateValue(null, it, new ModifiedValue(it.getCustomFieldValue(predjenoKilometaraField),  utoceno / razlika * 100),changeHolder) 
}