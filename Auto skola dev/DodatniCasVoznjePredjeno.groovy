import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import groovy.json.*
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZavrsenCasVoznje")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def linkMgr = ComponentAccessor.getIssueLinkManager()

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-6518")

def brojOdobrenihCasovaField = cfManager.getCustomFieldObject("customfield_10912")
def brojOdvezenihCasovaField = cfManager.getCustomFieldObject("customfield_11606")
def brojPreostalihCasovaField = cfManager.getCustomFieldObject("customfield_11226")
def stvPocetakCasaField = cfManager.getCustomFieldObject("customfield_10915")
def zeljenoTrajanjeField = cfManager.getCustomFieldObject("customfield_10918")
def stvTrajanjeField = cfManager.getCustomFieldObject("customfield_10917")
def stvKrajCasaField = cfManager.getCustomFieldObject("customfield_10916")
def trajanjeCasaField = cfManager.getCustomFieldObject("customfield_10921")
def odvezenBrMinDopField = cfManager.getCustomFieldObject("customfield_11406")
def trajanjeCasa = issue.getCustomFieldValue(trajanjeCasaField)
def pocetakCasa = issue.getCustomFieldValue(stvPocetakCasaField)
def krajCasa = issue.getCustomFieldValue(stvKrajCasaField)
def stvPocetakCasaMins = pocetakCasa.getTime() / 1000 / 60
def stvKrajCasaMins = krajCasa.getTime() / 1000 / 60
def stvTrajanje = stvKrajCasaMins - stvPocetakCasaMins

stvTrajanjeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(stvTrajanjeField), (double)stvTrajanje), changeHolder)

def kandidatIssue = issue.getParentObject()
def odvezeniDopunski = kandidatIssue.getSubTaskObjects().findAll { it != issue && it.getIssueTypeId() == "10601" && it.getStatusId() == "10313" }
def brojMinuta = stvTrajanje ?: (double)0
odvezeniDopunski.each { 	
	log.debug(it)
	log.debug("stvarno: " + it.getCustomFieldValue(stvTrajanjeField).toString())
	log.debug("nominalno: " + it.getCustomFieldValue(trajanjeCasaField).toString())
	brojMinuta += it.getCustomFieldValue(stvTrajanjeField) // ?: it.getCustomFieldValue(trajanjeCasaField)
}

def zeljenoTrajanje = kandidatIssue.getCustomFieldValue(zeljenoTrajanjeField)
def brojOdobrenihCasova = kandidatIssue.getCustomFieldValue(brojOdobrenihCasovaField)
def brojOdvezenihCasova = (brojMinuta / zeljenoTrajanje).round()
def brojPreostalihCasova = brojOdobrenihCasova - brojOdvezenihCasova

odvezenBrMinDopField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(odvezenBrMinDopField), (double)brojMinuta), changeHolder)
brojOdvezenihCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojOdvezenihCasovaField), (double)brojOdvezenihCasova), changeHolder)
brojPreostalihCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojPreostalihCasovaField), (double)brojPreostalihCasova), changeHolder)

def vozilo = kandidatIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324")) // Vozilo issue field

def baseUrl = "https://asteam.ddns.net:8085/api/reports/route?deviceId="
def deviceId = vozilo?.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11225"))
if (deviceId) {
	def authString = "YWRtaW46a2Vha29l"	
	def from = getFormattedStr(pocetakCasa.toString())
	def to = getFormattedStr(krajCasa.toString())
	def url = baseUrl + deviceId + "&groupId=1&from=$from&to=$to"
	def connection = url.toURL().openConnection()
	try {
		connection.addRequestProperty("Authorization", "Basic $authString")
		connection.addRequestProperty("Content-Type", "application/json")
		connection.addRequestProperty("Accept", "application/json")
		connection.setRequestMethod("GET")
		connection.connect()		
		def response = connection.content.text
		def json = new JsonSlurper().parseText(response)

		def start = json[0].attributes.totalDistance.toDouble()
		def end = json[json.size() - 1].attributes.totalDistance.toDouble()
		def distance = ((end - start) / 1000).round(2)
		log.debug(distance)
		def predjanoKilometaraField = cfManager.getCustomFieldObject("customfield_10513")
		predjanoKilometaraField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(predjanoKilometaraField), distance), changeHolder)
	} catch (ex) {
		log.debug(ex.getMessage())
	}
}

def getFormattedStr(dateTime) {
	def hour = dateTime.split(" ")[1].split(":")[0]
	def minute = dateTime.split(" ")[1].split(":")[1]

	return dateTime.split(" ")[0] + "T$hour" + ":$minute" + ":00Z"
}