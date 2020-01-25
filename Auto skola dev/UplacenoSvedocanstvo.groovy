import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.UplacenoSvedocanstvo")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def svedocanstvoField = cfManager.getCustomFieldObject("customfield_11275")
def svedocanstvo = issue.getCustomFieldValue(svedocanstvoField)
log.debug("Uplatiti za svedocanstvo: " + svedocanstvo)

def uplacenoSvedocanstvoField = cfManager.getCustomFieldObject("customfield_11413")
def uplacenoSvedocanstvo = issue.getCustomFieldValue(uplacenoSvedocanstvoField)
log.debug("Uplaceno: " + uplacenoSvedocanstvo)

if(svedocanstvo != 0){
	//uplacenoSvedocanstvoField.updateValue(null, issue, new ModifiedValue(uplacenoSvedocanstvo, 12301), changeHolder)
	issue.setCustomFieldValue(uplacenoSvedocanstvoField, 12301)
} else {
	//uplacenoSvedocanstvoField.updateValue(null, issue, new ModifiedValue(uplacenoSvedocanstvo, 12300), changeHolder)
	issue.setCustomFieldValue(uplacenoSvedocanstvoField, 12300)
}
log.debug("Uplaceno1: " + issue.getCustomFieldValue(uplacenoSvedocanstvoField))