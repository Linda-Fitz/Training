import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.jira.issue.ModifiedValue
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.activation.FileDataSource
import com.atlassian.jira.issue.Issue
import javax.activation.DataHandler
import javax.activation.DataSource
import java.text.SimpleDateFormat 
import com.atlassian.mail.Email
import javax.mail.Multipart
import java.io.File 
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.MailPotvrdaMaila")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-379")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def emailConfirmedField = cfManager.getCustomFieldObject("customfield_11001")
def fieldConfig = emailConfirmedField.getRelevantConfig(issue)
def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'Da'}

// Static
summary = issue.summary
Date now = new Date(System.currentTimeMillis())
nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()

// Load HTML template
String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/PotvrdaMaila.html').text
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)

def email = issue.getCustomFieldValue(emailField)
if (!email) {
  emailConfirmedField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(emailConfirmedField), da), new DefaultIssueChangeHolder())
  return
}
def template = bindTemplate(issue)
sendEmail(email, "Obavje\u0161tenje", template.toString())

def bindTemplate(Issue issue) {
  def binding = [
    issueid: issue.getId().toString(),
    projectname: issue.getProjectObject().getName(),
    issuekey: issue.getKey(),
    summary: summary,
    datum: nowFormatted,
  ]

  return template.make(binding) 
}


def sendEmail(emailAddr, subject, body) {
  SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
  if (mailServer) {
    Email email = new Email(emailAddr)
    email.setSubject(subject)
    //email.setBody(body)
    email.setMimeType("text/html")
    email.setEncoding("UTF-8")
    Multipart multipart = new MimeMultipart()
    MimeBodyPart bodyPart = new MimeBodyPart()
    bodyPart.setContent(body, "text/html; charset=utf-8")
    multipart.addBodyPart(bodyPart)
    multipart.addBodyPart(createImageBodyPart("C:/xampp/htdocs/img/visol.png", "<logo>"))
    email.setMultipart(multipart)
    mailServer.send(email)
  } else {
    log.error("No default SMTP server")
  }
}

def createImageBodyPart(String filepath, String cid) {
  def imageBodyPart = new MimeBodyPart();
  //DataSource fds = new FileDataSource(filepath)
    //imageBodyPart.setDataHandler(new DataHandler(fds))
	def file = new File(filepath)
	imageBodyPart.attachFile(file)
    //imageBodyPart.setFileName(filepath.split("/")[-1])
  imageBodyPart.setDisposition(MimeBodyPart.INLINE)
  imageBodyPart.setHeader("Content-ID", cid)
  
  return imageBodyPart  
}

// Color: AS 7f0814, MiraV 099bc2