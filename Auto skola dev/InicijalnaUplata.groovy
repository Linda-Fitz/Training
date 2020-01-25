import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import java.sql.Timestamp
import java.util.Date
import java.text.SimpleDateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.InicijalnaUplata")
log.setLevel(Level.DEBUG)

DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALY);
f = new DecimalFormat("#,##0.00", symbols)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def constantManager = ComponentAccessor.getConstantsManager()
def projectManager = ComponentAccessor.getProjectManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
def subTaskManager = ComponentAccessor.getSubTaskManager()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByKey("VISOL")
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-138")

def vrstaPlacanjaField = cfManager.getCustomFieldObject("customfield_10320")

def datumPrveUplateField = cfManager.getCustomFieldObject("customfield_10407")
def datumUplateField = cfManager.getCustomFieldObject("customfield_10803")
def iznosUplateField = cfManager.getCustomFieldObject("customfield_10424")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def viberField = cfManager.getCustomFieldObject("customfield_10221")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def ukupnoUplateField = cfManager.getCustomFieldObject("customfield_10806")
def iznosRateField = cfManager.getCustomFieldObject("customfield_10801") 
def preostaloField = cfManager.getCustomFieldObject("customfield_10800") 
def datumDospjecaField = cfManager.getCustomFieldObject("customfield_10802")
def brojRataField = cfManager.getCustomFieldObject("customfield_10639")
def zavodniBrojField = cfManager.getCustomFieldObject("customfield_10321")
def uplacenaTeorijaField = cfManager.getCustomFieldObject("customfield_10637")
def cijenaTeorijeField = cfManager.getCustomFieldObject("customfield_10503")
def vrstaLicneField = cfManager.getCustomFieldObject("customfield_10200")
def brojLicneField = cfManager.getCustomFieldObject("customfield_10216")
def izdataUField = cfManager.getCustomFieldObject("customfield_10121")
def ocevoImeField = cfManager.getCustomFieldObject("customfield_10117")
def kategorijaField = cfManager.getCustomFieldObject("customfield_10203")
def datumLjekarskogField = cfManager.getCustomFieldObject("customfield_10212")
def brojLjekarskogField = cfManager.getCustomFieldObject("customfield_10215")
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_10627")
def zaPlatitiRedovnaField = cfManager.getCustomFieldObject("customfield_11256")
def zaPlatitiDodDopField = cfManager.getCustomFieldObject("customfield_11303")
def zaPlatitiUkupnoField = cfManager.getCustomFieldObject("customfield_11302")

def zaPlatitiRedovna = (issue.getCustomFieldValue(zaPlatitiRedovnaField)) ? issue.getCustomFieldValue(zaPlatitiRedovnaField) : (double)0
def zaPlatitiDodDop = (issue.getCustomFieldValue(zaPlatitiDodDopField)) ? issue.getCustomFieldValue(zaPlatitiDodDopField) : (double)0
if (zaPlatitiDodDop == 0) {
	zaPlatitiDodDopField.updateValue(null, issue, new ModifiedValue(null, (double)0), changeHolder)
}

def zaPlatitiUkupno = zaPlatitiRedovna + zaPlatitiDodDop
zaPlatitiUkupnoField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zaPlatitiUkupnoField), zaPlatitiUkupno), changeHolder)


def brojRata = issue.getCustomFieldValue(brojRataField)
def datumPrveUplate = issue.getCustomFieldValue(datumPrveUplateField)
def datumUplate = issue.getCustomFieldValue(datumUplateField)
def iznosUplate = issue.getCustomFieldValue(iznosUplateField)
def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)
def viber = issue.getCustomFieldValue(viberField)
def email = issue.getCustomFieldValue(emailField)
def zavodniBroj = issue.getCustomFieldValue(zavodniBrojField)
def cijenaTeorije = (issue.getCustomFieldValue(cijenaTeorijeField)) ? issue.getCustomFieldValue(cijenaTeorijeField).toInteger() : (double) 0.00
def vrstaLicne = issue.getCustomFieldValue(vrstaLicneField)
def brojLicne = issue.getCustomFieldValue(brojLicneField)
def izdataU = issue.getCustomFieldValue(izdataUField)
def ocevoIme = issue.getCustomFieldValue(ocevoImeField)
def kategorija = issue.getCustomFieldValue(kategorijaField)
def datumLjekarskog = issue.getCustomFieldValue(datumLjekarskogField)
def brojLjekarskog = issue.getCustomFieldValue(brojLjekarskogField)
def brojPolaganja = issue.getCustomFieldValue(brojPolaganjaField)

if (cijenaTeorije == 0) {
	def fieldConfig = uplacenaTeorijaField.getRelevantConfig(issue)
	def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'DA'}
	uplacenaTeorijaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(uplacenaTeorijaField), da),changeHolder)
} else {
	def fieldConfig = uplacenaTeorijaField.getRelevantConfig(issue)
	def ne = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'NE'}
	uplacenaTeorijaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(uplacenaTeorijaField), ne),changeHolder)
}

def datumDospjecaUplate = datumPrveUplate.toString().split(" ")[0]
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

if (!issue.getCustomFieldValue(vrstaPlacanjaField).toString().contains("Na rate")) {
	return
}

//brojRata -= 1
brojRataField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojRataField), brojRata),changeHolder)

def iznosRate = issue.getCustomFieldValue(preostaloField) / brojRata
def iznosRateString = f.format(iznosRate)

if (!issue.getSubTaskObjects().find{ it.getIssueTypeId() == "10303" }) {
	brojRata.times { i ->
		if (i != 0) {
			datumDospjecaUplate = incrementMonth(datumDospjecaUplate)
		}
		Date date = sdf.parse(datumDospjecaUplate)
		Timestamp datumDospjecaUplateVal = new Timestamp(date.getTime())
		String datestr = date.format("dd.MM.yyyy")
		MutableIssue rataIssue = issueFactory.getIssue()
		rataIssue.setSummary("Rata pla\u0107anja | $ime $prezime | $datestr | \u20ac 0,00/$iznosRateString")
    	rataIssue.setParentObject(issue)
		rataIssue.setProjectObject(KAN)
		rataIssue.setIssueTypeId("10303")
		rataIssue.setCustomFieldValue(imeField, ime)
		rataIssue.setCustomFieldValue(prezimeField, prezime)
		rataIssue.setCustomFieldValue(telefonField, telefon)
		rataIssue.setCustomFieldValue(viberField, viber)
		rataIssue.setCustomFieldValue(emailField, email)
		rataIssue.setCustomFieldValue(iznosRateField, iznosRate)
		rataIssue.setCustomFieldValue(preostaloField, iznosRate)
		//rataIssue.setCustomFieldValue(zavodniBrojField, zavodniBroj)
		rataIssue.setCustomFieldValue(datumDospjecaField, datumDospjecaUplateVal)
		rataIssue.setCustomFieldValue(vrstaLicneField, vrstaLicne)
		rataIssue.setCustomFieldValue(brojLicneField, brojLicne)
		rataIssue.setCustomFieldValue(izdataUField, izdataU)
		rataIssue.setCustomFieldValue(ocevoImeField, ocevoIme)
		rataIssue.setCustomFieldValue(kategorijaField, kategorija)
		rataIssue.setCustomFieldValue(datumLjekarskogField, datumLjekarskog)
		rataIssue.setCustomFieldValue(brojLjekarskogField, brojLjekarskog)
		rataIssue.setCustomFieldValue(brojPolaganjaField, brojPolaganja)
		rataIssue.setAssignee(currentUser)
		rataIssue.setReporter(visol)
		Map<String,Object> rataIssueParams = ["issue" : rataIssue] as Map<String,Object>
		issueManager.createIssueObject(currentUser, rataIssueParams)
    	subTaskManager.createSubTaskIssueLink(issue, rataIssue, currentUser)
		issueManager.updateIssue(visol, rataIssue, EventDispatchOption.ISSUE_UPDATED, false)
	}
	// Reset datum uplate, iznos uplate 
	datumUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUplateField), null),changeHolder)
	iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
} 
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)

def incrementMonth(String date) {
	year = date.split("-")[0]
	month = date.split("-")[1]
	day = date.split("-")[2]

	if (month == "12") {
		month = "01"
		year = (year.toInteger() + 1).toString()
	} else {
		month = String.format("%02d", (month.toInteger() + 1))
	}

	if (day == "31") {
		if (month == "04" || month == "06" || month == "09" || month == "11") {
			day = "30"
		} 
	}

	if (month == "02" && day.toInteger() > 28) {
		if (isPrestupna()) {
			day = "29"
		} else {
			day = "28"
		}
	}
	return year + "-" + month + "-" + day
}

def isPrestupna(String year) {
	def yearint = year.toInteger()

	if (yearint % 400 == 0) {
		return true
	} else if (yearint % 100 != 0 && yearint % 4 == 0) {
		return true
	} else {
		return false
	}
}
