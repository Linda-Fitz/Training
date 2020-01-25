import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.UplataKondicioni")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def currentUserName = currentUser.getDisplayName()
def changeHolder = new DefaultIssueChangeHolder()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-2188")
def iznosUplateField = cfManager.getCustomFieldObject("customfield_10424")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
def specUplataField = cfManager.getCustomFieldObject("customfield_10804")
def zapLatitiField =cfManager.getCustomFieldObject("customfield_11303")
def ukupanIznosField =cfManager.getCustomFieldObject("customfield_10806")
def iznosUplate = issue.getCustomFieldValue(iznosUplateField)
def zaPlatiti = issue.getCustomFieldValue(zapLatitiField)
def preostalo = issue.getCustomFieldValue(preostaloField)
def ukupanIznos = issue.getCustomFieldValue(ukupanIznosField)
if (!iznosUplate) { 
	return
}
ukupno = ukupanIznos + iznosUplate
preostaloUplate = zaPlatiti - ukupno
preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostaloUplate),changeHolder)
iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
ukupanIznosField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(ukupanIznosField), ukupno),changeHolder)
//zapLatitiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zapLatitiField), preostaloUplate),changeHolder)
DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALY);
f = new DecimalFormat("#,###.00", symbols)

if (preostaloUplate < 0) {
	def prekoraceniIznosField = cfManager.getCustomFieldObject("customfield_10819")
    preostaloUplate1 = ukupno - zaPlatiti
	//def vecprekoraceniIznos = (parentIssue.getCustomFieldValue(prekoraceniIznosField)) ? parentIssue.getCustomFieldValue(prekoraceniIznosField) : (double)0
    //log.debug(vecprekoraceniIznos)
	def prekoraceniIznosTotal =  preostaloUplate1
    log.debug(prekoraceniIznosTotal)
	prekoraceniIznosField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prekoraceniIznosField), prekoraceniIznosTotal.round(2)),changeHolder)
    preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), null),changeHolder)
    
}
def specUplata = issue.getCustomFieldValue(specUplataField) 
def sad = new Timestamp(System.currentTimeMillis())
def sadStr = formatDatum(sad)
def specUplataUpdate = (specUplata) ? specUplata + ("\n" + sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName") : sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName"
specUplataUpdate = specUplataUpdate.toString()
specUplataField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(specUplataField), specUplataUpdate),changeHolder)

def formatDatum(Timestamp datumIspita) {
	def datumIspitaStr = datumIspita.toString()
	def year = datumIspitaStr.split("-")[0]
	def month = datumIspitaStr.split("-")[1]
	def day = datumIspitaStr.split("-")[2].split(" ")[0]
	def hour = datumIspitaStr.split(" ")[1].split(":")[0]
	def minute = datumIspitaStr.split(" ")[1].split(":")[1]

	return "$day-$month-$year $hour:$minute"
}