import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.CheckGratisOnTransition")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()

def gratisField = cfManager.getCustomFieldObject("customfield_11107")
def fieldConfig = gratisField.getRelevantConfig(issue)
def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'Da'}
gratisField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(gratisField), da),changeHolder)

/*
def countGratis() {
	def gratisField = cfManager.getCustomFieldObject("customfield_11107")
	def count = 0
	issue.getSubTaskObjects().each {
		if (it.getCustomFieldValue(gratisField)) {
			count += 1
		}
	}
	return count
}
*/