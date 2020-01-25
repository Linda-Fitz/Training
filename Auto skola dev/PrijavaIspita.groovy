import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.PrijavaIspita")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
issueFactory = ComponentAccessor.getIssueFactory()
constantManager = ComponentAccessor.getConstantsManager()
projectManager = ComponentAccessor.getProjectManager()
subTaskManager = ComponentAccessor.getSubTaskManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
changeHolder = new DefaultIssueChangeHolder()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
 

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-20")

ispitField = cfManager.getCustomFieldObject("customfield_10814")
ispit = issue.getCustomFieldValue(ispitField)
def uplacenoField = cfManager.getCustomFieldObject("customfield_10637")
uplaceno = (issue.getCustomFieldValue(uplacenoField)) ? issue.getCustomFieldValue(uplacenoField).toString() : ""
def cijenaTeorijeField = cfManager.getCustomFieldObject("customfield_10503")
cijenaTeorije = (issue.getCustomFieldValue(cijenaTeorijeField)) ? issue.getCustomFieldValue(cijenaTeorijeField).toInteger() : 0

// Kreiranje linka
linkId = checkUplata()
brisanjePostojecegLinka(ispit, issue)
linkMgr.createIssueLink(issue.id, ispit.id, linkId, 1, visol)

// Datumi
datumIspitaField = cfManager.getCustomFieldObject("customfield_10624")
datumIspita = ispit.getCustomFieldValue(datumIspitaField)
datumUTdo = datumIspita - 15
datumUTod = datumIspita - 1
godina = datumIspita.toString().split("-")[0]
datumIstekaIspita = (isPrestupna(godina)) ? datumIspita + 366 : datumIspita + 365
sad = new Timestamp(System.currentTimeMillis())

// Broj polaganja	
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_10627")
def ispitTeorijeSubtaskovi = issue.getSubTaskObjects().findAll { it.getIssueTypeId() == "10405"}
def brojPolaganja = (ispitTeorijeSubtaskovi) ? ispitTeorijeSubtaskovi.size() : 0
brojPolaganja = brojPolaganja + 1
brojPolaganjaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPolaganjaField), brojPolaganja.toInteger().toString()), changeHolder)

def datumIstekaIspitaField = cfManager.getCustomFieldObject("customfield_10631")
def datumUTdoField = cfManager.getCustomFieldObject("customfield_10811")
def datumUTodField = cfManager.getCustomFieldObject("customfield_10812")
def datumPrijaveIspitaField = cfManager.getCustomFieldObject("customfield_10815")

datumUTdoField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUTdoField), datumUTdo),changeHolder)	
datumUTodField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUTodField), datumUTod),changeHolder)	
datumIstekaIspitaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumIstekaIspitaField), datumIstekaIspita),changeHolder)	
datumPrijaveIspitaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPrijaveIspitaField), sad),changeHolder)	

// Kreiranje subtaska
if (vecPostojiIspitTeorije(issue) == false) {
	def imeField = cfManager.getCustomFieldObject("customfield_10132")
	def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
	def telefonField = cfManager.getCustomFieldObject("customfield_10220")
	def viberField = cfManager.getCustomFieldObject("customfield_10221")
	def emailField = cfManager.getCustomFieldObject("customfield_10222")
	def zavodniBrojField = cfManager.getCustomFieldObject("customfield_10321")
	def vdKategorijaVozilaField = cfManager.getCustomFieldObject("customfield_10203")
	def adresaField = cfManager.getCustomFieldObject("customfield_10120")
	def vrstaLicneField = cfManager.getCustomFieldObject("customfield_10200")
	def brojLicneField = cfManager.getCustomFieldObject("customfield_10216")
	def izdataUField = cfManager.getCustomFieldObject("customfield_10121")
	def ocevoImeField = cfManager.getCustomFieldObject("customfield_10117")
	def kategorijaField = cfManager.getCustomFieldObject("customfield_10203")
	def datumLjekarskogField = cfManager.getCustomFieldObject("customfield_10212")
	def brojLjekarskogField = cfManager.getCustomFieldObject("customfield_10215")
    def datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")
    def datumPrijaveField = cfManager.getCustomFieldObject("customfield_10815")
	def polField = cfManager.getCustomFieldObject("customfield_10423")
	
	def ime = issue.getCustomFieldValue(imeField)
	def prezime = issue.getCustomFieldValue(prezimeField)
	def telefon = issue.getCustomFieldValue(telefonField)
	def viber = issue.getCustomFieldValue(viberField)
	def email = issue.getCustomFieldValue(emailField)
	def zavodniBroj = issue.getCustomFieldValue(zavodniBrojField)
	def vdKategorijaVozila = issue.getCustomFieldValue(vdKategorijaVozilaField)
	def adresa = ispit.getCustomFieldValue(adresaField)
	def datumIspitaStr = formatDatumIspitaStr(datumIspita)
	def vrstaLicne = issue.getCustomFieldValue(vrstaLicneField)
	def brojLicne = issue.getCustomFieldValue(brojLicneField)
	def izdataU = issue.getCustomFieldValue(izdataUField)
	def ocevoIme = issue.getCustomFieldValue(ocevoImeField)
	def kategorija = issue.getCustomFieldValue(kategorijaField)
	def datumLjekarskog = issue.getCustomFieldValue(datumLjekarskogField)
	def brojLjekarskog = issue.getCustomFieldValue(brojLjekarskogField)
	def datumRodjenja = issue.getCustomFieldValue(datumRodjenjaField)
	def pol = issue.getCustomFieldValue(polField)
    
	MutableIssue prijavljenIspitIssue = issueFactory.getIssue()
	prijavljenIspitIssue.setSummary("Prijavljen ispit teorije | $ime $prezime | $datumIspitaStr $adresa") 
	prijavljenIspitIssue.setParentObject(issue)
	prijavljenIspitIssue.setProjectObject(KAN)
	prijavljenIspitIssue.setIssueTypeId("10405")
	prijavljenIspitIssue.setAssignee(currentUser)
	prijavljenIspitIssue.setReporter(currentUser)
	prijavljenIspitIssue.setCustomFieldValue(datumIspitaField, datumIspita)
	prijavljenIspitIssue.setCustomFieldValue(imeField, ime)
	prijavljenIspitIssue.setCustomFieldValue(prezimeField, prezime)
	prijavljenIspitIssue.setCustomFieldValue(telefonField, telefon)
	prijavljenIspitIssue.setCustomFieldValue(viberField, viber)
	prijavljenIspitIssue.setCustomFieldValue(emailField, email)
    prijavljenIspitIssue.setCustomFieldValue(datumRodjenjaField, datumRodjenja)
    prijavljenIspitIssue.setCustomFieldValue(datumPrijaveField, sad)
	prijavljenIspitIssue.setCustomFieldValue(polField, pol)

	prijavljenIspitIssue.setCustomFieldValue(zavodniBrojField, zavodniBroj)
	prijavljenIspitIssue.setCustomFieldValue(vdKategorijaVozilaField, vdKategorijaVozila)
	prijavljenIspitIssue.setCustomFieldValue(adresaField, adresa)
	prijavljenIspitIssue.setCustomFieldValue(ispitField, ispit)
	prijavljenIspitIssue.setCustomFieldValue(vrstaLicneField, vrstaLicne)
	prijavljenIspitIssue.setCustomFieldValue(brojLicneField, brojLicne)
	
	prijavljenIspitIssue.setCustomFieldValue(izdataUField, izdataU)
	prijavljenIspitIssue.setCustomFieldValue(ocevoImeField, ocevoIme)
	prijavljenIspitIssue.setCustomFieldValue(kategorijaField, kategorija)

	prijavljenIspitIssue.setCustomFieldValue(datumLjekarskogField, datumLjekarskog)
	prijavljenIspitIssue.setCustomFieldValue(brojLjekarskogField, brojLjekarskog)
	prijavljenIspitIssue.setCustomFieldValue(brojPolaganjaField, brojPolaganja.toString())
	Map<String,Object> prijavljenIspitIssueParams = ["issue" : prijavljenIspitIssue] as Map<String,Object>
	issueManager.createIssueObject(visol, prijavljenIspitIssueParams)
	subTaskManager.createSubTaskIssueLink(issue, prijavljenIspitIssue, visol)
	
	if (linkId == 10300) {
		workflowTransitionUtil.setAction (11)	
	} else {
		workflowTransitionUtil.setAction (21)	
	}
	workflowTransitionUtil.setIssue(prijavljenIspitIssue)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()
}

def brisanjePostojecegLinka(Issue ispit, Issue issue) {
	for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
   		def linkedIssue = link.getDestinationObject()
   		if (linkedIssue == ispit) {
   			linkMgr.removeIssueLink(link, visol)
   		}
   	}
}

def vecPostojiIspitTeorije(Issue issue) {
	Boolean vecPostoji = false
	issue.getSubTaskObjects().each { subtask ->
		if (subtask.getIssueTypeId() == "10405") {  
			if (subtask.getCustomFieldValue(ispitField) == ispit) {
				vecPostoji = true
			}
		}
	}
	return vecPostoji	
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

def formatDatumIspitaStr(Timestamp datumIspita) {
	def datumIspitaStr = datumIspita.toString()
	def year = datumIspitaStr.split("-")[0]
	def month = datumIspitaStr.split("-")[1]
	def day = datumIspitaStr.split("-")[2].split(" ")[0]
	def hour = datumIspitaStr.split(" ")[1].split(":")[0]
	def minute = datumIspitaStr.split(" ")[1].split(":")[1]

	return "$day-$month-$year $hour:$minute"
}

def checkUplata() {	
	if (uplaceno == "DA") {
		return 10301
	} else if (uplaceno == "NE") {
		return 10300
	} else if (uplaceno == "DOZVOLJENO") {
		return 10401
	} else {
		if (cijenaTeorije == 0) {
			return 10301
		} else {
			return 10300
		}
	}
}