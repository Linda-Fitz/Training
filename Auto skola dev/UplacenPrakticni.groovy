import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.UplacenIspitPrakticni")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def prakticniField = cfManager.getCustomFieldObject("customfield_11264")
def prakticni = issue.getCustomFieldValue(prakticniField)
log.debug("Uplatiti za prakticni: " + prakticni)

def uplacenPrakticniField = cfManager.getCustomFieldObject("customfield_11263") 
def uplacenPrakticni = issue.getCustomFieldValue(uplacenPrakticniField)
log.debug("Uplaceno: " + uplacenPrakticni)

if(prakticni != 0){
	//uplacenPrakticniField.updateValue(null, issue, new ModifiedValue(uplacenPrakticni, 12132), changeHolder)
	issue.setCustomFieldValue(uplacenPrakticniField, 12132)
} else {
	//uplacenPrakticniField.updateValue(null, issue, new ModifiedValue(uplacenPrakticni, 12131), changeHolder)
	issue.setCustomFieldValue(uplacenPrakticniField, 12131)
}
log.debug("Uplaceno1: " + issue.getCustomFieldValue(uplacenPrakticniField))
