import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.jira.issue.ModifiedValue
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeBodyPart
import javax.activation.FileDataSource
import javax.mail.internet.MimeMessage
import com.atlassian.jira.issue.Issue
import javax.activation.DataHandler
import javax.activation.DataSource
import java.text.SimpleDateFormat 
import com.atlassian.mail.Email
import javax.mail.Multipart
import java.io.File 
import org.apache.log4j.Logger
import org.apache.log4j.Level

 
def log = Logger.getLogger("com.acme.MailObavjestenjeGrupaIspitaMirav")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3829")

def ispitField = cfManager.getCustomFieldObject("customfield_10814")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def adresaField = cfManager.getCustomFieldObject("customfield_10120")
def datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
def datumVrijeme = issue.getCustomFieldValue(datumVrijemeField)

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
vrijemeIspita = datumVrijeme.toString().split(" ")[1]
vrijemeIspita = vrijemeIspita.substring(0, vrijemeIspita.size() - vrijemeIspita.split(":")[-1].size() - 1)

def ispitLink  = linkMgr.getInwardLinks(issue.id).find{ it.getLinkTypeId() == 10501 } // Ispit teorija - grupa
def ispitIssue = ispitLink.getSourceObject()
adresaIspita = ispitIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10120"))

// Load HTML template
String fileContents = new File('C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/ObavjestenjeGrupaIspitTeorijaMirav.html').text 
template = new groovy.text.StreamingTemplateEngine().createTemplate(fileContents)

def kandidatiLinks = linkMgr.getInwardLinks(issue.id).findAll{ it.getLinkTypeId() == 10502 }
kandidatiLinks.each{ link ->
	def kandidat = link.getSourceObject()
	def email = kandidat.getCustomFieldValue(emailField)
	if (!email) {
		return
	}
	
	def template = bindTemplate(kandidat)
	sendEmail(email, "Obavje\u0161tenje - ispit teorija", template.toString())		
}

def bindTemplate(Issue issue) {
	def binding = [
		issueid: issue.getId().toString(),
		projectname: issue.getProjectObject().getName(),
		issuekey: issue.getKey(),
		summary: summary,
		description: description,
		datum: nowFormatted,
		datumIspita: datumIspita,
		adresaIspita: adresaIspita,
		vrijemeIspita: vrijemeIspita
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


