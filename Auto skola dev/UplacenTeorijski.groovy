import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.UplacenIspitTeorije")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()

def teorijskiField = cfManager.getCustomFieldObject("customfield_10503")
def teorijski = issue.getCustomFieldValue(teorijskiField)
log.debug("Uplatiti za teorijski: " + teorijski)

def uplacenTeorijskiField = cfManager.getCustomFieldObject("customfield_10637")
def uplacenTeorijski = issue.getCustomFieldValue(uplacenTeorijskiField)
log.debug("Uplaceno: " + uplacenTeorijski)

if(teorijski!=0){
	//uplacenTeorijskiField.updateValue(null, issue, new ModifiedValue(uplacenTeorijski, 11207), changeHolder)
	issue.setCustomFieldValue(uplacenTeorijskiField, 11207)
} else {
	//uplacenTeorijskiField.updateValue(null, issue, new ModifiedValue(uplacenTeorijski, 11206), changeHolder)
	issue.setCustomFieldValue(uplacenTeorijskiField, 11206)
}
log.debug("Uplaceno1: " + issue.getCustomFieldValue(uplacenTeorijskiField))
