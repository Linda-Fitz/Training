import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.jql.parser.JqlQueryParser
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

 
def log = Logger.getLogger("com.acme.MailObavjestenjeIspitPrakticni")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
attachmentManager = ComponentAccessor.getAttachmentManager()
def changeHolder = new DefaultIssueChangeHolder()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-51")
mailIssue = issue

def ispitField = cfManager.getCustomFieldObject("customfield_11265")
def emailField = cfManager.getCustomFieldObject("customfield_10222")

Date now = new Date(System.currentTimeMillis())
nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()

String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/IspitniTestovi.html').text
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = kandidat AND statusCategory != \"Done\""
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def kandidati = queryIssue.executeJQL(query)

for (i = kandidati.size() - 1; i >= 0; i--) {
    Issue kandidat = kandidati[i]
    def kStatus = kandidat.getStatusId()
    if (kStatus != "10100" && kStatus != "10102" && kStatus != "10201") { continue } 
    def email = kandidat.getCustomFieldValue(emailField)
    if (!email) { continue }
    log.debug(kandidat)
    def template = bindTemplate(kandidat)
    sendEmail(email, "Literatura za teorijski dio ispita", template.toString())
}
/*
def template = bindTemplate(issue)
email = "nikola.dragas@yahoo.com"
sendEmail(email , "Literatura za teorijski dio ispita", template.toString())
*/
def bindTemplate(Issue issue) {
    def binding = [
        issuekey: issue.getKey(),
        datum: nowFormatted,
    ]

    return template.make(binding)   
}

def sendEmail(emailAddr, subject, body) {
  SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
  if (mailServer) {
  try {
    Email email = new Email(emailAddr)
    email.setSubject(subject)
    email.setMimeType("multipart/mixed")
    email.setEncoding("UTF-8")
    Multipart multipart = new MimeMultipart()
    MimeBodyPart bodyPart = new MimeBodyPart()
    bodyPart.setContent(body, "text/html; charset=utf-8")
    multipart.addBodyPart(bodyPart)
    if (!addAttachments(email, multipart)) { 
        log.debug("Issue $issue nema attachment-a")
        return
    }
    multipart.addBodyPart(createImageBodyPart("C:/xampp/htdocs/img/visol.png", '<logo>'))
    email.setMultipart(multipart)
    mailServer.send(email)
return true
  } catch (err) {
    log.debug(err)
    return false
  }
  } else {
    log.error("No default SMTP server")
  }
}
def addAttachments(email, multipart) {
    
    List<Attachment> attachments = attachmentManager.getAttachments(mailIssue);
    if (attachments.size() == 0) { return false }
    attachments.each {
        def attachBody = new MimeBodyPart();
        def attachedFile = AttachmentUtils.getAttachmentFile(it)
        def attachedFileName = it.getFilename();
        FileDataSource source = new FileDataSource(attachedFile)
        attachBody.setDataHandler(new DataHandler(source))
        attachBody.setFileName(attachedFileName)
        multipart.addBodyPart(attachBody)
    }      
    return true
}
def createImageBodyPart(String filepath, String cid) {
    def imageBodyPart = new MimeBodyPart();

    def file = new File(filepath)
    imageBodyPart.attachFile(file)
    imageBodyPart.setFileName(filepath.split("/")[-1])
    imageBodyPart.setDisposition(MimeBodyPart.INLINE)
    imageBodyPart.setHeader("Content-ID", cid)
    return imageBodyPart    
}

// Color: AS 7f0814, MiraV 099bc2