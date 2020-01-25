import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.jira.util.io.InputStreamConsumer
import groovy.text.StreamingTemplateEngine
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat 
import org.apache.log4j.Level
import org.apache.log4j.Logger
 
def log = Logger.getLogger("com.acme.WebPanel")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def attachmentManager = ComponentAccessor.getAttachmentManager()
def targetSize = 600
def issue = context.issue as Issue
def templateEngine = new StreamingTemplateEngine()

List<Attachment> attachments = attachmentManager.getAttachments(issue);
log.debug("All attachments: " + attachments.size().toString())
 
def url = ""
def images = attachments.findAll { it.getMimetype().contains("image/") && !it.getFilename().contains("Droid-") }
images.each {
	def id = it.getId().toString()
	log.debug(id)
	def name = it.getFilename()
	log.debug(name)
	writer.write("<img src=\"https://visoldev.ddns.net:8000/secure/attachment/${id}/${name}\" width=200px/>")
}


/*
fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Templates/Attachment.html').text
def template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)
def binding = [
	attach : url
]
mailText = template.make(binding)
writer.write(mailText)
*/