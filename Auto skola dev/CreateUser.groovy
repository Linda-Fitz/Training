import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Level
import org.apache.log4j.Logger

log = Logger.getLogger("com.acme.CreateUser")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def userUtil = ComponentAccessor.getUserUtil()
def visol = userUtil.getUserByName("VISOL")
UserService userService = ComponentAccessor.getComponent(UserService.class)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-1125")
//MutableIssue issue = issue

def nameField = cfManager.getCustomFieldObject("customfield_10132")
def lastNameField = cfManager.getCustomFieldObject("customfield_10133")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def usernameField = cfManager.getCustomFieldObject("customfield_10820")
def userField = cfManager.getCustomFieldObject("customfield_10207")

def name = issue.getCustomFieldValue(nameField)
def lastName = issue.getCustomFieldValue(lastNameField)
def usernameF = issue.getCustomFieldValue(usernameField)
def emailAddress = (issue.getCustomFieldValue(emailField)) ? issue.getCustomFieldValue(emailField) : ""
if (!name || !lastName) { return }
log.debug(usernameF)

def username = (name + "." + lastName).toLowerCase()
def displayName = name + " " + lastName

//def users = userUtil.getAllUsersInGroupNames(["jira-software-users"])
def users = userUtil.getAllApplicationUsers()
def postojeciUser = users.find { it.getEmailAddress() == emailAddress }

if (!postojeciUser) {
	userUtil.createUserNoNotification(username, "", emailAddress, displayName, null)
	def createdUser = userUtil.getUserByName(username)
	def validationResult = userService.validateRemoveUserFromApplication(visol, createdUser, com.atlassian.jira.application.ApplicationKeys.SOFTWARE)
	userService.removeUserFromApplication(validationResult)
	issue.setAssignee(createdUser)
	issue.setReporter(currentUser)
	issue.setCustomFieldValue(usernameField, username)
	issue.setCustomFieldValue(userField, createdUser)
	issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)
}
else {
  issue.setAssignee(postojeciUser)
  issue.setReporter(currentUser)
  issue.setCustomFieldValue(userField, postojeciUser)
  issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)
}


