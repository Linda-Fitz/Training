import com.atlassian.jira.component.ComponentAccessor

def parent = issue.getParentObject()

def brCasova = parent.getSubTaskObjects().findAll{ it.getStatusId() == "10501" || it.getStatusId() == "10313" || it.getStatusId() == "10312"}.size()
def brOdobrenihCasovaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10912")
def brOdobrenihCasova = parent.getCustomFieldValue(brOdobrenihCasovaField)

if (brCasova < brOdobrenihCasova) { passesCondition = false }