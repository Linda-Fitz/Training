import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.workflow.WorkflowTransitionUtil
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

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-6978")

def simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")

def emailField = cfManager.getCustomFieldObject("customfield_10222")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10802")

//log.debug(razlika)

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = \"rata pla\u0107anja\" AND status = OTVORENO AND cf[10802] = startOfDay(3)"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)
log.debug("Issues: " + issues)  

for (i = issues.size() - 1; i >= 0; i--) {
  if(issues[i].getIssueTypeId() != "10303") { continue }
    MutableIssue dospjelaRata = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issues[i].key)
    log.debug("Dospjela rata: " + dospjelaRata.getKey())  
    def parent =  issues[i].getParentObject()
    def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
    preostalo = parent.getCustomFieldValue(preostaloField)
    log.debug(preostalo)

    Date now = new Date(System.currentTimeMillis())

    danas = simpleDateFormat.format(new Date(now.getTime())).toString()
    dan = danas.split("/")[0]
    mesec = danas.split("/")[1]
    godina = danas.split("/")[2]
    //log.debug(dan)
    //log.debug(mesec)
    //log.debug(godina)
    
    def danRate = dospjelaRata.getCustomFieldValue(datumVrijemeField)
    razlika = (dan.toInteger() + 3).toString() + "/" + mesec + "/" + godina
    log.debug("razlika: "+ razlika)
    
      // Load HTML template
      String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeODospjecuRate.html').text
      template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)

        nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()
        def email = dospjelaRata.getCustomFieldValue(emailField)
        log.debug(email)
        if (!email) { return }
        
        def template = bindTemplate(dospjelaRata)
        sendEmail(email, "Obavje\u0161tenje o dospije\u0107u rate", template.toString())
      }
	//} 

def bindTemplate(Issue issue) {
  def binding = [
    issueid: issue.getId().toString(),
    projectname: issue.getProjectObject().getName(),
    issuekey: issue.getKey(),
    summary: summary,
    datum: nowFormatted,
    datumDospjecaRate: razlika,
        preostaloZaUplatu: preostalo,
    
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

// Color: AS 7f0814, MiraV 099bc2