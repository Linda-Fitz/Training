import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.web.action.issue.DeleteIssue
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.UserPropertyManager

import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.SetValueFromProperty")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("ce-173")
def  userPropertyManager = ComponentAccessor.getUserPropertyManager()

def nameValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.NAME')
def telValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.TEL')
def websiteValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.WEBSITE')
def emailValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.EMAIL')
def adressValue = userPropertyManager.getPropertySet(visol)?.getString('jira.meta.ADR')

String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeODospjecuRate.html').text
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)
def template = bindTemplate(dospjelaRata)

def bindTemplate(Issue issue) {
	def binding = [
		name:nameValue,
		telephone: telValue,
		website: websiteValue,
		email: emailValue,
		adress: adressValue
	]

	return template.make(binding)	
}

