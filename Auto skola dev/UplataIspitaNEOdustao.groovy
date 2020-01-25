import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.UplataIspitaNE")
log.setLevel(Level.DEBUG)

cfManager = ComponentAccessor.getCustomFieldManager()

Boolean proceed = true
issue.getSubTaskObjects().each{ subtask ->
	if (subtask.getIssueTypeId() == "10405") {
		proceed = false
	}
}

if (!proceed) {return}

def uplacenaTeorijaField = cfManager.getCustomFieldObject("customfield_10637")
def fieldConfig = uplacenaTeorijaField.getRelevantConfig(issue)
def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
uplacenaTeorijaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(uplacenaTeorijaField), ne),new DefaultIssueChangeHolder())