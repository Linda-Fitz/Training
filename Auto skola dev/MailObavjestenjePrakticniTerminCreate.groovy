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
def changeHolder = new DefaultIssueChangeHolder()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-379")

def ispitField = cfManager.getCustomFieldObject("customfield_11265")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def datumVrijemePoligonField = cfManager.getCustomFieldObject("customfield_11260")
def datumVrijemePoligon = issue.getCustomFieldValue(datumVrijemePoligonField)
def datumVrijemeGradskaField = cfManager.getCustomFieldObject("customfield_11261")
def datumVrijemeGradska = issue.getCustomFieldValue(datumVrijemeGradskaField)

// Static
summary = issue.summary
description = ""
if (issue.getDescription()) {
  description = '<tr style="height: 26px; padding: 0px 15px 0px 16px;"><td class="value" style="border: 1px solid #cccccc; padding: 0px 15px 0px 16px; height: 26px; width: 100%; color: #707070; font-style: normal; font-variant: normal; font-weight: normal; font-stretch: normal; font-size: 18px; line-height: 14px; font-family: "Calibri", sans-serif; text-align: left; background-color: #f0f0f0;">' + issue.getDescription() + '</td></tr>'
}
Date now = new Date(System.currentTimeMillis())
nowFormatted = new SimpleDateFormat("dd.MM.yyyy").format(now).toString()
datumIspitaPoligon = datumVrijemePoligon.toString().split(" ")[0]
datumIspitaPoligon = datumIspitaPoligon.split("-")[2] + "." + datumIspitaPoligon.split("-")[1] + "." + datumIspitaPoligon.split("-")[0]
vrijemeIspitaPoligon = datumVrijemePoligon.toString().split(" ")[1]
vrijemeIspitaPoligon = vrijemeIspitaPoligon.substring(0, vrijemeIspitaPoligon.size() - vrijemeIspitaPoligon.split(":")[-1].size() - 1)
datumIspitaGradska = datumVrijemeGradska.toString().split(" ")[0]
datumIspitaGradska = datumIspitaGradska.split("-")[2] + "." + datumIspitaGradska.split("-")[1] + "." + datumIspitaGradska.split("-")[0]
vrijemeIspitaGradska = datumVrijemeGradska.toString().split(" ")[1]
vrijemeIspitaGradska = vrijemeIspitaGradska.substring(0, vrijemeIspitaGradska.size() - vrijemeIspitaGradska.split(":")[-1].size() - 1)

// Load HTML template
String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeIspitPrakticniDatum.html').text
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
  if (kStatus != "10202" && kStatus != "10306" && kStatus != "10308" && kStatus != "10307" && kStatus != "10309" && kStatus != "10310" && kStatus != "10700") { continue } //Voznja-redovna obuka, Voznja redovna obuka zavrsena, voznja- ispit prijavljen, voznja-nije polozio, voznja-dodatna obuka, voznja - dodatna obuka zavrsena, spreman za ispit

  ispitField.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(ispitField), issue), changeHolder)
  def email = kandidat.getCustomFieldValue(emailField)
  
  if (!email) { continue }

  def template = bindTemplate(kandidat)
  sendEmail(email, "Obavje\u0161tenje - prakti\u010dni ispit", template.toString())
}

def bindTemplate(Issue issue) {
  def binding = [
    issueid: issue.getId().toString(),
    projectname: issue.getProjectObject().getName(),
    issuekey: issue.getKey(),
    summary: summary,
    description: description,
    datum: nowFormatted,
    datumIspitaPoligon: datumIspitaPoligon,
    vrijemeIspitaPoligon: vrijemeIspitaPoligon,
    datumIspitaGradska: datumIspitaGradska,
    vrijemeIspitaGradska: vrijemeIspitaGradska
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