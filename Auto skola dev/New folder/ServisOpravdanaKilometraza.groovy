import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.mail.Email
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import java.text.SimpleDateFormat
import groovy.json.*
import java.sql.Timestamp
import java.util.Date
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ServisOpravdanaKilometraza")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)

def predjanoKilometaraField = cfManager.getCustomFieldObject("customfield_10513")
def voziloField = cfManager.getCustomFieldObject("customfield_10324")

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = Vozila AND status = aktivno"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def vozila = queryIssue.executeJQL(query)

def table = "<table style=\"width:100%\" border=\"1\";><body>"
def header = "<tr><th></th>"
def headerLine2 = "<tr><th></th>"
def body = ""

def sviDaniOpravdano = [:]
def sviDaniTotal = [:]
7.times { number ->
    def dayModifier = 6 - number
    String query1 = "issuetype in (\"Dopunski \u010Das\",\"Redovni \u010Das\",\"Dodatni \u010Das\") AND status = zavr\u0161eno and cf[10920] > startOfday(-$dayModifier) and cf[10920] > endOfday(-$dayModifier) "
    def queryIssue1 = shell.parse(new File("$basePath/general.groovy"))
    def casovi = queryIssue1.executeJQL(query1)
    def vozilaOpravdano = [:]
    def vozilaTotal = [:]
    casovi.each {
        def cas = issueManager.getIssueByKeyIgnoreCase(it.key)
        def predjeno = (cas.getCustomFieldValue(predjanoKilometaraField)) ? cas.getCustomFieldValue(predjanoKilometaraField) : (double)0

        def vozilo = cas.getCustomFieldValue(voziloField)
        if (!vozilo) { 
            log.debug("Nema vozila za cas $cas")
            return 
        }
        if (vozilo && vozilaOpravdano."$vozilo") {
            vozilaOpravdano."${vozilo.key}" += predjeno
        } else if (vozilo) {
            vozilaOpravdano.put(vozilo.key, predjeno)
        }
    }

    def day = new Date(System.currentTimeMillis() - (dayModifier * 1000 * 3600 * 24))
    def datumTableFormat = new SimpleDateFormat("dd.MM.yyyy").format(day).toString()
    header += "<th colspan=\"2\">$datumTableFormat</th>"
    headerLine2 += "<th colspan=\"1\">Opravdano</th><th colspan=\"1\">Ukupno</th>"

    vozilaOpravdano.each { voziloKey, opravdano ->
        def voziloIssue = issueManager.getIssueByKeyIgnoreCase(voziloKey)
        def baseUrl = "https://asteam.ddns.net:8085/api/reports/route?deviceId="
        def deviceId = voziloIssue?.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11225"))

        if (deviceId) {
            def authString = " "

            def dayFormatted = new SimpleDateFormat("yyyy-MM-dd").format(day).toString()
            def from = dayFormatted + "T00:00:00Z"
            def to = dayFormatted + "T23:59:00Z"
            def url = baseUrl + deviceId + "&groupId=1&from=$from&to=$to"
            //log.debug(url)
            def connection = url.toURL().openConnection()
            try {
                connection.addRequestProperty("Authorization", "Basic $authString")
                connection.addRequestProperty("Content-Type", "application/json")
                connection.addRequestProperty("Accept", "application/json")
                connection.setRequestMethod("GET")
                connection.connect()        
                def response = connection.content.text
                def json = new JsonSlurper().parseText(response)

                def distance
                if (json.size() > 0) {
                    def start = json[0].attributes.totalDistance.toDouble()
                    def end = json[json.size() - 1].attributes.totalDistance.toDouble()
                    distance = ((end - start) / 1000).round(2)                  
                } else {
                    distance = 0
                }

                if (vozilaTotal."$voziloKey") {
                    vozilaTotal."$voziloKey" += distance
                } else {
                    vozilaTotal.put(voziloKey, distance)
                }
            } catch (ex) {
                log.debug(ex.getMessage())
            }
        }
    }   
    sviDaniOpravdano.put(datumTableFormat, vozilaOpravdano)
    sviDaniTotal.put(datumTableFormat, vozilaTotal)
}

header += "<th colspan=\"2\">Total</th>"
headerLine2 += "<th colspan=\"1\">Opravdano</th><th colspan=\"1\">Ukupno</th>"

vozila.each { vozilo -> 
    def key = vozilo.key
    def summary = vozilo.summary
    body += "<tr><td style=\"text-align:center\">$summary</td>" 
log.debug(key)
    def opravdanoAll = 0
    def totalAll = 0
    sviDaniOpravdano.each { opravdan ->
        def dateKey = opravdan.getKey()
        log.debug(dateKey)
        def opravdano = (opravdan.getValue()."$key") ? opravdan.getValue()."$key".toString() : "0"
        log.debug(opravdano)
        def total = (sviDaniTotal."$dateKey"."$key") ? sviDaniTotal."$dateKey"."$key".toString() : "0"
        body += "<td style=\"text-align:center\">$opravdano</td>"
        body += "<td style=\"text-align:center\">$total</td>"

        opravdanoAll += opravdano.toDouble()
        totalAll += total.toDouble()
    }
    opravdanoAll = opravdanoAll.round(2)
    totalAll = totalAll.round(2)
    body += "<td style=\"text-align:center\">$opravdanoAll</td>"
    body += "<td style=\"text-align:center\">$totalAll</td>"    
    body += "</tr>" 
}
header += "</tr>"
headerLine2 += "</tr>"
body += "</body>"

html = table + header + headerLine2 + body
//return html
//sendEmail("luka.radonjic@ivisol.com; sanja.laban@ivisol.com", "Nedjeljni izvještaj", html)
sendEmail("nikola.dragas@yahoo.com", "Nedjeljni izvještaj", html)
def sendEmail(emailAddr, subject, body) {
  SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
  if (mailServer) {
    Email email = new Email(emailAddr)
    email.setSubject(subject)
    email.setBody(body)
    email.setMimeType("text/html")
    email.setEncoding("UTF-8")
    mailServer.send(email)
  } else {
    log.error("No default SMTP server")
  }
}