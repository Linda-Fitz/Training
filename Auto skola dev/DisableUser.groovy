import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.user.UserService
import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.UserWithAttributes
import com.atlassian.crowd.embedded.impl.ImmutableUser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Level
import org.apache.log4j.Logger

log = Logger.getLogger("com.acme.DisableUser")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def userUtil = ComponentAccessor.getUserUtil()
def visol = userUtil.getUserByName("VISOL")
CrowdService crowdService = ComponentAccessor.crowdService
UserService userService = ComponentAccessor.getComponent(UserService.class)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()


//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-574")

def nameField = cfManager.getCustomFieldObject("customfield_10132")
def lastNameField = cfManager.getCustomFieldObject("customfield_10133")
def emailField = cfManager.getCustomFieldObject("customfield_10222")

def name = issue.getCustomFieldValue(nameField)
def lastName = issue.getCustomFieldValue(lastNameField)
def emailAddress = (issue.getCustomFieldValue(emailField)) ? issue.getCustomFieldValue(emailField) : ""
if (!name || !lastName) { return }
def username = (name + "." + lastName).toLowerCase()

def adminGroup = ComponentAccessor.groupManager.getGroup("jira-administrators")
if (ComponentAccessor.groupManager.getUserNamesInGroup(adminGroup).contains(username) == true) { return }

UserWithAttributes user = crowdService.getUserWithAttributes(username)
def updateUser = ApplicationUsers.from(ImmutableUser.newUser(user).active(false).toUser())
def updateUserValidationResult = userService.validateUpdateUser(updateUser)
if (updateUserValidationResult.isValid()) {
    userService.updateUser(updateUserValidationResult)
}