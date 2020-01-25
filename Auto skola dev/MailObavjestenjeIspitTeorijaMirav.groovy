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

def log = Logger.getLogger("com.acme.MailObavjestenjeTeorija")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3938")

def ispitField = cfManager.getCustomFieldObject("customfield_10814")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def adresaField = cfManager.getCustomFieldObject("customfield_10120")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def datumVrijeme = issue.getCustomFieldValue(datumVrijemeField)
adresaIspita = issue.getCustomFieldValue(adresaField)

// Static
summary = issue.summary
description = ""
if (issue.getDescription()) {
  description = '<tr style="height: 26px; padding: 0px 15px 0px 16px;"><td class="value" style="border: 1px solid #cccccc; padding: 0px 15px 0px 16px; height: 26px; width: 100%; color: #707070; font-style: normal; font-variant: normal; font-weight: normal; font-stretch: normal; font-size: 18px; line-height: 14px; font-family: "Calibri", sans-serif; text-align: left; background-color: #f0f0f0;">' + issue.getDescription() + '</td></tr>'
}
Date now = new Date(System.currentTimeMillis())
nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()
datumIspita = datumVrijeme.toString().split(" ")[0]
datumIspita = datumIspita.split("-")[2] + "." + datumIspita.split("-")[1] + "." + datumIspita.split("-")[0]

// Load HTML template
String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeIspitTeorijaMirav.html').text
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query =  "issuetype = Kandidat AND status in (\"10305\",\"10304\")"// TEORIJA- OBUKA ZAVRSENA. TEPROJA- NIJE POLOZIO/LA
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def kandidati = queryIssue.executeJQL(query)

kandidati.each {
  log.debug(it)
}

for (i = kandidati.size() - 1; i >= 0; i--) {
  Issue kandidat = kandidati[i]
  ispitField.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(ispitField), issue), new DefaultIssueChangeHolder())
  def email = kandidat.getCustomFieldValue(emailField)
  if (!email) {
    continue
  }
  /*if (!email.contains("@ivisol.com")) {
    continue
  }
*/
  def template = bindTemplate(kandidat)
  sendEmail(email, "Obavje\u0161tenje - ispit teorija", template.toString())
}

// obavjestenje na grupe

def bindTemplate(Issue issue) {
  def binding = [
    issueid: issue.getId().toString(),
    projectname: issue.getProjectObject().getName(),
    issuekey: issue.getKey(),
    summary: summary,
    description: description,
    datum: nowFormatted,
    datumIspita: datumIspita,
    adresaIspita: adresaIspita
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