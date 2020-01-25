import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue

def currentUserName = ComponentAccessor.getJiraAuthenticationContext().getUser().getUsername()
def groupManager = ComponentAccessor.groupManager
def tipZaposlenog = issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10308"))
def jiraAdminGroup = groupManager.getGroup("jira-administrators")
def ownerGroup = groupManager.getGroup("Vlasnik")
def ceoGroup = groupManager.getGroup("Direktor")
def adminGroup = groupManager.getGroup("Administrator")



if (groupManager.getUserNamesInGroup(adminGroup).contains(currentUserName) == true) {
	passesCondition = true
} else if (groupManager.getUserNamesInGroup(ownerGroup)?.contains(currentUserName) == true) {
	passesCondition = true
} else if (groupManager.getUserNamesInGroup(ceoGroup)?.contains(currentUserName) == true) {
	if (tipZaposlenog?.findAll { it.toString() == "Vlasnik"} ) {
		passesCondition = false
	} else {
		passesCondition = true
	}
} else if (groupManager.getUserNamesInGroup(adminGroup)?.contains(currentUserName) == true) {
	if (tipZaposlenog?.findAll { it.toString() == "Vlasnik" || it.toString() == "Direktor" || it.toString() == "Administrator" } ) {
		passesCondition = false
	} else {
		passesCondition = true
	}
} else if (tipZaposlenog?.findAll { it.toString() == "Instruktor" }) {
	passesCondition = true
} else {
	passesCondition = false
}