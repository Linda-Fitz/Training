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

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-5471")

ispitField = cfManager.getCustomFieldObject("customfield_11265")
ispit = issue.getCustomFieldValue(ispitField)
def uplacenoField = cfManager.getCustomFieldObject("customfield_11263") 
uplaceno = (issue.getCustomFieldValue(uplacenoField))? issue.getCustomFieldValue(uplacenoField).toString() : ""
def cijenaPrakticniField = cfManager.getCustomFieldObject("customfield_11264")
cijenaPrakticni = (issue.getCustomFieldValue(cijenaPrakticniField)) ? issue.getCustomFieldValue(cijenaPrakticniField).toInteger() : 0

// (re)Kreiranje linka
linkId = checkUplata()
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
	if (link.getDestinationObject() == ispit) {
		if (link.getLinkTypeId() != linkId) {
			linkMgr.removeIssueLink(link, visol)
		} 
		break
	}
}
linkMgr.createIssueLink(issue.id, ispit.id, linkId, 1, visol)

// Broj prijavljenih 
def brojPrijavljenihField = cfManager.getCustomFieldObject("customfield_11262")
def prijavljeni = linkMgr.getInwardLinks(ispit.id).findAll{ it.getLinkTypeId() == 10701 }
def brojPrijavljenih = prijavljeni.size() + 1
brojPrijavljenihField.updateValue(null, ispit, new ModifiedValue(ispit.getCustomFieldValue(brojPrijavljenihField), (double)brojPrijavljenih), new DefaultIssueChangeHolder())

// Datumi
datumIspitaField = cfManager.getCustomFieldObject("customfield_11260")
datumIspita = ispit.getCustomFieldValue(datumIspitaField)
sad = new Timestamp(System.currentTimeMillis())

// Broj polaganja	
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_11266")
def ispitPrakticniSubtaskovi = issue.getSubTaskObjects().findAll { it.getIssueTypeId() == "10705"}
def brojPolaganja = (ispitPrakticniSubtaskovi) ? ispitPrakticniSubtaskovi.size() : 0
brojPolaganja = brojPolaganja + 1
brojPolaganjaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPolaganjaField), (double)brojPolaganja), changeHolder)

def datumPrijaveIspitaField = cfManager.getCustomFieldObject("customfield_10815")
datumPrijaveIspitaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPrijaveIspitaField), sad),changeHolder)	

// Prakticni ispit, uplacen prakticni, svi datumi na svijet i u bg, prakticni- broj polaganja

// Kreiranje subtaska
if (!ispitPrakticniSubtaskovi.findAll{ it.getCustomFieldValue(ispitField) == ispit }) {
	def imeField = cfManager.getCustomFieldObject("customfield_10132")
	def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
	def polField = cfManager.getCustomFieldObject("customfield_10423")
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
	def datumPoligonaField = cfManager.getCustomFieldObject("customfield_11267")
	def datumVoznjeField = cfManager.getCustomFieldObject("customfield_11268")
	def datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")
	def datumUTDoField = cfManager.getCustomFieldObject("customfield_10812")
	def datumUTOdField = cfManager.getCustomFieldObject("customfield_10811")
	def datumPolozenogIspitaField = cfManager.getCustomFieldObject("customfield_10632")
	def dodijeljeniInstruktorField = cfManager.getCustomFieldObject("customfield_10913")
	def datumPocetkaField = cfManager.getCustomFieldObject("customfield_12311")
	def datumZavrsetkaField = cfManager.getCustomFieldObject("customfield_12310")
	
	def ime = issue.getCustomFieldValue(imeField)
	def prezime = issue.getCustomFieldValue(prezimeField)
	def pol = issue.getCustomFieldValue(polField)
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
    def datumPoligona = issue.getCustomFieldValue(datumPoligonaField)
	def datumVoznje = issue.getCustomFieldValue(datumVoznjeField)
	def datumRodjenja = issue.getCustomFieldValue(datumRodjenjaField)
	def datumUTDo = issue.getCustomFieldValue(datumUTDoField)
	def datumUTOd = issue.getCustomFieldValue(datumUTOdField)
	def datumPolozenogIspita = issue.getCustomFieldValue(datumPolozenogIspitaField)
	def dodijeljeniInstruktor = issue.getCustomFieldValue(dodijeljeniInstruktorField)
	def datumPocetka = issue.getCustomFieldValue(datumPocetkaField)
	def datumZavrsetka = issue.getCustomFieldValue(datumZavrsetkaField)
	
	MutableIssue prijavljenIspitIssue = issueFactory.getIssue()
	prijavljenIspitIssue.setSummary("Prijavljen prakti\u010dni ispit | $ime $prezime | $datumIspitaStr $adresa") 
	prijavljenIspitIssue.setParentObject(issue)
	prijavljenIspitIssue.setProjectObject(KAN)
	prijavljenIspitIssue.setIssueTypeId("10705")
	prijavljenIspitIssue.setAssignee(visol)
	prijavljenIspitIssue.setReporter(visol)
	prijavljenIspitIssue.setCustomFieldValue(imeField, ime)
	prijavljenIspitIssue.setCustomFieldValue(prezimeField, prezime)
	prijavljenIspitIssue.setCustomFieldValue(polField, pol)
	prijavljenIspitIssue.setCustomFieldValue(telefonField, telefon)
	prijavljenIspitIssue.setCustomFieldValue(viberField, viber)
	prijavljenIspitIssue.setCustomFieldValue(emailField, email)
	
	prijavljenIspitIssue.setCustomFieldValue(datumPoligonaField, datumPoligona)
	prijavljenIspitIssue.setCustomFieldValue(datumVoznjeField, datumVoznje)
	
	prijavljenIspitIssue.setCustomFieldValue(datumPocetkaField, datumPocetka)
	prijavljenIspitIssue.setCustomFieldValue(datumZavrsetkaField, datumZavrsetka)
	
	prijavljenIspitIssue.setCustomFieldValue(zavodniBrojField, zavodniBroj)
	prijavljenIspitIssue.setCustomFieldValue(vdKategorijaVozilaField, vdKategorijaVozila)
	prijavljenIspitIssue.setCustomFieldValue(adresaField, adresa)
	prijavljenIspitIssue.setCustomFieldValue(ispitField, ispit)
	prijavljenIspitIssue.setCustomFieldValue(vrstaLicneField, vrstaLicne)
	prijavljenIspitIssue.setCustomFieldValue(brojLicneField, brojLicne)
	prijavljenIspitIssue.setCustomFieldValue(datumRodjenjaField, datumRodjenja)
	prijavljenIspitIssue.setCustomFieldValue(datumUTDoField, datumUTDo)
	prijavljenIspitIssue.setCustomFieldValue(datumUTOdField, datumUTOd)
	prijavljenIspitIssue.setCustomFieldValue(datumPolozenogIspitaField, datumPolozenogIspita)
	prijavljenIspitIssue.setCustomFieldValue(dodijeljeniInstruktorField,dodijeljeniInstruktor)
	
	prijavljenIspitIssue.setCustomFieldValue(izdataUField, izdataU)
	prijavljenIspitIssue.setCustomFieldValue(ocevoImeField, ocevoIme)
	prijavljenIspitIssue.setCustomFieldValue(kategorijaField, kategorija)
	prijavljenIspitIssue.setCustomFieldValue(datumLjekarskogField, datumLjekarskog)
	prijavljenIspitIssue.setCustomFieldValue(brojLjekarskogField, brojLjekarskog)
	prijavljenIspitIssue.setCustomFieldValue(brojPolaganjaField, (double)brojPolaganja)
	Map<String,Object> prijavljenIspitIssueParams = ["issue" : prijavljenIspitIssue] as Map<String,Object>
	issueManager.createIssueObject(visol, prijavljenIspitIssueParams)
	subTaskManager.createSubTaskIssueLink(issue, prijavljenIspitIssue, visol)
	linkMgr.createIssueLink(prijavljenIspitIssue.id, ispit.id, 10701, 1, visol)
	
return
	
	if (linkId == 10507) { // nije uplacen
		workflowTransitionUtil.setAction (11)	
	} else {
		workflowTransitionUtil.setAction (21)	
	}
	workflowTransitionUtil.setIssue(prijavljenIspitIssue)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()
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
		return 10506
	} else if (uplaceno == "NE") {
		return 10507
	} else if (uplaceno == "DOZVOLJENO") {
		return 10505
	} else {
		if (cijenaPrakticni == 0) {
			return 10506
		} else {
			return 10507
		}
	}
}