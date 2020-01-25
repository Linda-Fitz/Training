import com.atlassian.jira.component.ComponentAccessor

def parent = issue.getParentObject()
def cfManager = ComponentAccessor.getCustomFieldManager()

def odobrenBrMinDopField = cfManager.getCustomFieldObject("customfield_11405")
def odvezenBrMinDopField = cfManager.getCustomFieldObject("customfield_11406")
odobrenBrMinDop = issue.getCustomFieldValue(odobrenBrMinDopField)
odvezenBrMinDop = (issue.getCustomFieldValue(odvezenBrMinDopField)) ? issue.getCustomFieldValue(odvezenBrMinDopField) : 0

if (odvezenBrMinDop > odobrenBrMinDop) { 
	passesCondition = false
} else {
	passesCondition = true
}