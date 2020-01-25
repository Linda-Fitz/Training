import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.mail.Email
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import org.apache.commons.lang.RandomStringUtils
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import javax.mail.internet.MimeMultipart
import javax.activation.FileDataSource
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.Multipart
import java.sql.Timestamp
import javax.activation.DataHandler
import javax.activation.DataSource
import java.text.SimpleDateFormat 
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.ServisCasKod")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()

searchService = ComponentAccessor.getComponent(SearchService.class)
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-3395") 

shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts"
logger = shell.parse(new File("$basePath/Logger.groovy"))
visol = ComponentAccessor.getUserUtil().getUserByName("VISOL")
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

startField = cfManager.getCustomFieldObject("customfield_10919")
endField = cfManager.getCustomFieldObject("customfield_10920")
startCodeField = cfManager.getCustomFieldObject("customfield_12100")
endCodeField = cfManager.getCustomFieldObject("customfield_12390")
emailField = cfManager.getCustomFieldObject("customfield_10222")
changeHolder = new DefaultIssueChangeHolder()
issuetypes = "10601, 10802, 10703"

// issuetypes = "\"Dodatni čas\", \"Redovni čas\", \"Dopunski čas\""
nowLong = System.currentTimeMillis()

getPendingClasses().each { sendCode(it.key, true) }
getFinishingClasses().each { sendCode(it.key, false) }

def sendCode(String issueKey, boolean start) {
  def issue = issueManager.getIssueObject(issueKey)
  def kod = generateCode()
  def emailAddress = getEmailAddress(issue)

  if (!emailAddress) { return }
  if (issue.getCustomFieldValue(startCodeField) && start) { return }
  if (issue.getCustomFieldValue(endCodeField) && !start) { return }

  if (emailCode(kod, emailAddress, start, issue)) {
    logger.log(this, "Poslat kod ${kod} na adresu ${emailAddress}, issue ${issue.key}")
    if (start) {
      startCodeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(startCodeField), kod), changeHolder)
    } else {
      endCodeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(endCodeField), kod), changeHolder)
    }
  } 
  
}
def getEmailAddress(issue) {
  def parent = issue.getParentObject()
  if (issue.getIssueTypeId() == "10601") {
    return parent.getCustomFieldValue(emailField)
  } else {
    def kandidatLink = linkMgr.getInwardLinks(parent.id).find { it.getLinkTypeId() == 10504 }
    return kandidatLink.getSourceObject().getCustomFieldValue(emailField)
  }
}

def generateCode() {
  String charset = (('A'..'Z') + ('0'..'9')).join()
  return RandomStringUtils.random(4, charset.toCharArray())
}

def isLessThan15MinsDiff(Timestamp scheduled) {
  return ((scheduled.getTime() - nowLong) / 60000) < 15
}

def result

//GroovyShell shell = new GroovyShell()

def getPendingClasses() {
 basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
 String query = "issuetype in ($issuetypes) AND cf[10919] > now() AND cf[10919] < startOfDay(1) AND status = zakazano and cf[12100] is EMPTY"
 def queryIssue = shell.parse(new File("$basePath/general.groovy"))
  result = queryIssue.executeJQL(query)
  return result.findAll { 
    issueK =  issueManager.getIssueObject(it.key)
    //log.debug(issueK)
    isLessThan15MinsDiff(issueK.getCustomFieldValue(startField))
  }
}

def getFinishingClasses() {

  basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
 String query1 = "issuetype in ($issuetypes) AND cf[10920] > now() AND cf[10920] < startOfDay(1) AND status = \"10312\" and cf[12390] is EMPTY"
 def queryIssue1 = shell.parse(new File("$basePath/general.groovy"))
 result = queryIssue1.executeJQL(query1)
  return result.findAll { 
    issueK = issueManager.getIssueObject(it.key)
    //log.debug(issueK)
    isLessThan15MinsDiff(issueK.getCustomFieldValue(endField))
  }
}

/*
def getPendingClasses() {
  SearchService.ParseResult parseResult = searchService.parseQuery(visol, "issuetype in ($issuetypes) AND cf[10919] > now() AND cf[10919] < startOfDay(1) AND status = zakazano and cf[12100] is EMPTY")
  def searchResult = searchService.search(visol, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
  def result = searchResult.getIssues().collect { issueManager.getIssueObject(it.key) }
  return result.findAll { 
    isLessThan15MinsDiff(it.getCustomFieldValue(startField))
  }
}

def getFinishingClasses() {
  SearchService.ParseResult parseResult = searchService.parseQuery(visol, "issuetype in ($issuetypes) AND cf[10920] > now() AND cf[10920] < startOfDay(1) AND status = \"10312\" and cf[12390] is EMPTY") //dodala sam preko id za status zapocet ,jer nije htelo onako 
  def searchResult = searchService.search(visol, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
  def result = searchResult.getIssues().collect { issueManager.getIssueObject(it.key) }
  return result.findAll { 
    isLessThan15MinsDiff(it.getCustomFieldValue(endField))
  }
}
*/

def emailCode(String code, String emailAddress, boolean start, Issue issue) {
  SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
  def pocetakKraj = (start) ? "po\u010Detak" : "kraj" 
  def subject = "Kod za \u010Das"
  def map = [
    pocetakKraj: pocetakKraj,
    codeStart: code
  ]
  def body = bindTemplate('ObavjestenjeKodCasa', issue, map)
 //log.debug(body)
  try {
    Email email = new Email(emailAddress)
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
    return true
  } catch (err) {
    log.debug(err)
    return false
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

def bindTemplate(String templateName, Issue issue, Map argsMap) {
  def templateFile = new File("C:/Program Files/Atlassian/Application Data/JIRA/scripts/Template/${templateName}.html").text
  def template = new groovy.text.StreamingTemplateEngine().createTemplate(templateFile)

  Date now = new Date()
  def sdf = new SimpleDateFormat("dd.MM.yyyy")
  def nowFormatted = sdf.format(now).toString()

  def binding = [
    projectname: issue.getProjectObject().getName(),
    issuekey: issue.getKey(),
    datum: nowFormatted,
  ]

  binding << argsMap

  return template.make(binding).toString()
}