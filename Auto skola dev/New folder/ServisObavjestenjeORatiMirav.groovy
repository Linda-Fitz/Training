import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import javax.mail.internet.MimeMultipart
import com.atlassian.jira.util.JiraUtils
import javax.mail.internet.MimeBodyPart
import javax.activation.FileDataSource
import javax.mail.internet.MimeMessage
import com.atlassian.jira.issue.Issue
import javax.activation.DataHandler
import javax.activation.DataSource
import java.text.SimpleDateFormat 
import com.atlassian.mail.Email
import org.apache.log4j.Logger
import org.apache.log4j.Level
import javax.mail.Multipart
import java.time.LocalDate
import java.sql.Timestamp
 
def log = Logger.getLogger("com.acme.MailObavjestenjeRataMirav")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

def simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10802")

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "status = OTVORENO AND cf[10802] = startOfDay(3)"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)
log.debug("Issues: " + issues)  

for (i = issues.size() - 1; i >= 0; i--) {
        MutableIssue dospjelaRata = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issues[i].key)
        if(dospjelaRata.getIssueTypeId() != "10303") { continue }
        log.debug("Dospjela rata: " + dospjelaRata.getKey())    
        def parent =  dospjelaRata.getParentObject()
        def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
        preostalo = parent.getCustomFieldValue(preostaloField)
        log.debug(preostalo)

String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeODospjecuRateMirav.html').text
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)
    
   Date now = new Date(System.currentTimeMillis())
   nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()
   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
   datumD = dospjelaRata.getCustomFieldValue(datumVrijemeField)
   datumDospijeca = sdf.format(datumD).toString()
   email = dospjelaRata.getCustomFieldValue(emailField)
if (!email) { return }  
    
def template = bindTemplate(dospjelaRata)
sendEmail(email, "Obavje\u0161tenje o dospije\u0107u rate", template.toString())

}
def bindTemplate(Issue issue) {
    def binding = [
        issueid: issue.getId().toString(),
        projectname: issue.getProjectObject().getName(),
        issuekey: issue.getKey(),
        summary: issue.summary,
        datum: nowFormatted,
        datumDospjecaRate: datumDospijeca,
        preostaloZaUplatu: preostalo,
        
    ]

    return template.make(binding)   
}

def sendEmail(email1, subject, body) {
  SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
  if (mailServer) {
    Email email = new Email(email1)
    email.setSubject(subject)
    email.setMimeType("text/html")
    email.setEncoding("UTF-8")
    Multipart multipart = new MimeMultipart()
    MimeBodyPart bodyPart = new MimeBodyPart()
    bodyPart.setContent(body, "text/html; charset=utf-8")
    multipart.addBodyPart(bodyPart)
    multipart.addBodyPart(createImageBodyPart("C:/xampp/htdocs/img/Mirav.png", "<logo>"))
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
