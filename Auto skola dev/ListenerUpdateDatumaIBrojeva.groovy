if (issue.getIssueTypeId() != "10200" ) { return } //kandidat

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import java.text.SimpleDateFormat

import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.PromenaVrednostiPoljaListener")
log.setLevel(Level.DEBUG)

def brojLicneIspraveChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Broj li")}
log.debug("Broj licne changed: "+brojLicneIspraveChanged)

def brojLekarskogUverenjaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Broj ljekarskog uvjerenja")}
log.debug("Broj lekarskog changed: "+brojLekarskogUverenjaChanged)

def datumLekarskogUverenjaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Datum ljekarskog uvjerenja")}
log.debug("Datum lekarskog changed: "+datumLekarskogUverenjaChanged)

def datumPocetkaPrakticneObukeChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Datum po")}
log.debug("Datum pocetka prakticne changed: "+datumPocetkaPrakticneObukeChanged)

def datumZavrsetkaPrakticneObukeChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Datum zavr")}
log.debug("Datum kraja prakticne changed: "+datumZavrsetkaPrakticneObukeChanged)

def datumTeorijskogIspitaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("ispita teorije")}
log.debug("Datum polozenog teorijskog: "+datumTeorijskogIspitaChanged)

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def issueManager = ComponentAccessor.getIssueManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")

def brojLicneIspraveString = brojLicneIspraveChanged?.newstring
log.debug("broj licne isprave: " + brojLicneIspraveString)

def brojLekarskogUverenjaString = brojLekarskogUverenjaChanged?.newstring
log.debug("broj lekarskog uverenja: " + brojLekarskogUverenjaString)

def datumLekarskogUverenjaString = datumLekarskogUverenjaChanged?.newstring
log.debug("datum lekarskog uverenja: " + datumLekarskogUverenjaString)

def datumPocetkaPrakticneString = datumPocetkaPrakticneObukeChanged?.newstring
log.debug("datum pocetka prakticne: " + datumPocetkaPrakticneString)

def datumZavrsetkaPrakticneString = datumZavrsetkaPrakticneObukeChanged?.newstring
log.debug("datum zavrsetka prakticne: " + datumZavrsetkaPrakticneString)

def datumZavrsetkaTeorijskeString = datumTeorijskogIspitaChanged?.newstring
log.debug("datum polozene teorije: " + datumZavrsetkaTeorijskeString)

def prijavljenIspit = issue.getSubTaskObjects().findAll { it.getIssueTypeId() == "10705" }
log.debug(prijavljenIspit)

prijavljenIspit.each{
	
	log.debug("Prijavljen ispit: " + it)
	MutableIssue prijavljenIspitIssue = (MutableIssue) ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(it.toString())
	
	def datumLekarskogField = cfManager.getCustomFieldObject("customfield_10212")
	def datumLek = prijavljenIspitIssue.getCustomFieldValue(datumLekarskogField)
	log.debug("datum lekarskog: " + datumLek)
	
	def brojLicneFild = cfManager.getCustomFieldObject("customfield_10216")
	def brojLic = prijavljenIspitIssue.getCustomFieldValue(brojLicneFild)
	log.debug("broj licne isprave: " + brojLic)
	
	def brojLekarskogField = cfManager.getCustomFieldObject("customfield_10215")
	def brojLekar = prijavljenIspitIssue.getCustomFieldValue(brojLekarskogField)
	log.debug("Broj lekarskog: " + brojLekar)
	
	def datumPocetkaPrakticneField = cfManager.getCustomFieldObject(12311)
	def datumPocetka = prijavljenIspitIssue.getCustomFieldValue(datumPocetkaPrakticneField)
	log.debug("prakticna pocetak: " + datumPocetka)
	
	def datumZavrsetkaPrakticneField = cfManager.getCustomFieldObject(12310)
	def datumZavrsetka = prijavljenIspitIssue.getCustomFieldValue(datumZavrsetkaPrakticneField)
	log.debug("prakticna kraj: " + datumZavrsetka)
	
	def datumZavrsetkaTeorijskeField = cfManager.getCustomFieldObject(12310)
	def datumZavrsetkaTeorijske = prijavljenIspitIssue.getCustomFieldValue(datumZavrsetkaTeorijskeField)
	log.debug("teorija kraj: " + datumZavrsetkaTeorijske)
	
	Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(datumLekarskogUverenjaString)
	log.debug(date1)
	Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(datumPocetkaPrakticneString)
	log.debug(date2)
	Date date3 = new SimpleDateFormat("dd/MM/yyyy").parse(datumZavrsetkaPrakticneString)
	log.debug(date3)
	Date date4 = new SimpleDateFormat("dd/MM/yyyy").parse(datumZavrsetkaPrakticneString)
	log.debug(date4)
		
		
	if (datumLekarskogUverenjaString != null || brojLicneIspraveString != null || brojLekarskogUverenjaString != null || datumPocetkaPrakticneString != null || datumZavrsetkaPrakticneString != null || datumZavrsetkaTeorijskeString != null) {
	    datumLekarskogField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(datumLek, date1.toTimestamp()), changeHolder)
	    brojLicneFild.updateValue(null, prijavljenIspitIssue, new ModifiedValue(brojLic, brojLicneIspraveString), changeHolder)
		brojLekarskogField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(brojLekar, brojLekarskogUverenjaString), changeHolder)
		datumPocetkaPrakticneField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(datumPocetka, date2.toTimestamp()), changeHolder)
		datumZavrsetkaPrakticneField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(datumZavrsetka, date3.toTimestamp()), changeHolder)
		datumZavrsetkaTeorijskeField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(datumZavrsetkaTeorijske, date4.toTimestamp()), changeHolder)
		
	}
	issueManager.updateIssue(visol, prijavljenIspitIssue, EventDispatchOption.ISSUE_UPDATED, false)
}
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)