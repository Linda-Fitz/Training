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
def parent = issue.getParentObject()
def vozilo = parent.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324")) // Vozilo issue field
log.debug("Vozilo: " + vozilo.key)

def stvPocetakCasaField = cfManager.getCustomFieldObject("customfield_10915")
def stvKrajCasaField = cfManager.getCustomFieldObject("customfield_10916")
def stvPocetakCasa = issue.getCustomFieldValue(stvPocetakCasaField)
def stvKrajCasa = issue.getCustomFieldValue(stvKrajCasaField)
def stvPocetakCasaMins = stvPocetakCasa.getTime() / 1000 / 60
def stvKrajCasaMins = stvKrajCasa.getTime() / 1000 / 60

// GENERISANJE TRACKING LINKA
def baseUrl = "https://asteam.ddns.net:8085/api/reports/route?deviceId="
def deviceId = vozilo?.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11225"))
if (deviceId) {
	log.debug("deviceId: $deviceId")
	def authString = "YWRtaW46a2Vha29l"	
	def from = getFormattedStr(stvPocetakCasa.toString())
	def to = getFormattedStr(stvKrajCasa.toString())
	def url = baseUrl + deviceId + "&groupId=6&from=$from&to=$to"
	def connection = url.toURL().openConnection()
	Boolean hasTracking = true
	try {
		connection.addRequestProperty("Authorization", "Basic $authString")
		connection.addRequestProperty("Content-Type", "application/json")
		connection.addRequestProperty("Accept", "application/json")
		connection.setRequestMethod("GET")
		connection.connect()		
		def response = connection.content.text
		def json = new JsonSlurper().parseText(response)

		log.debug("response: $response")
		if (json.size() > 0) {
			def start = json[0].attributes.totalDistance.toDouble()
			log.debug(start)
			def end = json[json.size() - 1].attributes.totalDistance.toDouble()
			log.debug(end)
			def distance = ((end - start) / 1000).round(2)
			def predjanoKilometaraField = cfManager.getCustomFieldObject("customfield_10513")
			predjanoKilometaraField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(predjanoKilometaraField), distance), changeHolder)			
		} else {
			hasTracking = false
		}
	} catch (ex) {
		log.debug(ex.getMessage())
	}

	// TRACCAR URL
	if (hasTracking) {
		def trackUrl = "https://asteam.ddns.net:3000/Traccar.html?"
		trackUrl += "deviceId=$deviceId&as=$authString&from=$from&to=$to"
		log.debug("Issue " + issue.key + " track url: $trackUrl")
		def trackingUrlField = cfManager.getCustomFieldObject("customfield_11224")
		trackingUrlField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(trackingUrlField), trackUrl), changeHolder)		
	}
}


def getFormattedStr(dateTime) {
	def hour = dateTime.split(" ")[1].split(":")[0]
	def minute = dateTime.split(" ")[1].split(":")[1]

	return dateTime.split(" ")[0] + "T$hour" + ":$minute" + ":00Z"  
}