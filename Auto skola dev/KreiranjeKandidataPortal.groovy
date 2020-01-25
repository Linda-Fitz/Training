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


def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def constantManager = ComponentAccessor.getConstantsManager()
def projectManager = ComponentAccessor.getProjectManager()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
def subTaskManager = ComponentAccessor.getSubTaskManager()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}

//Issue issue = issueManager.getIssueByKeyIgnoreCase("PZK-1")

def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def viberField = cfManager.getCustomFieldObject("customfield_10221")
def emailField = cfManager.getCustomFieldObject("customfield_10222")
def vrstaLicneField = cfManager.getCustomFieldObject("customfield_10200")
def brojLicneField = cfManager.getCustomFieldObject("customfield_10216")
def izdataUField = cfManager.getCustomFieldObject("customfield_10121")
def ocevoImeField = cfManager.getCustomFieldObject("customfield_10117")
def kategorijaField = cfManager.getCustomFieldObject("customfield_10203")
def datumLjekarskogField = cfManager.getCustomFieldObject("customfield_10212")
def brojLjekarskogField = cfManager.getCustomFieldObject("customfield_10215")
def slobodnoVrijemeVoznjaField = cfManager.getCustomFieldObject("customfield_10328")
def slobodnoVrijemeTeorijaField = cfManager.getCustomFieldObject("customfield_10910")
def zeljeniInstruktorField = cfManager.getCustomFieldObject("customfield_10318")
def datumIstekaLjekarskogField = cfManager.getCustomFieldObject("customfield_10417")
def postanskiBrojField = cfManager.getCustomFieldObject("customfield_10325")
def adresaField = cfManager.getCustomFieldObject("customfield_10120")
def zanimanjeField = cfManager.getCustomFieldObject("customfield_10136")
def drzavaField = cfManager.getCustomFieldObject("customfield_10827")
def drzavljanstvoField = cfManager.getCustomFieldObject("customfield_11217")
def mjestoRodjenjaField = cfManager.getCustomFieldObject("customfield_10119")
def polField = cfManager.getCustomFieldObject("customfield_10423")
def datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")
def jmbgField = cfManager.getCustomFieldObject("customfield_10219")
def datumField = cfManager.getCustomFieldObject("customfield_10315")

def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)
def viber = issue.getCustomFieldValue(viberField)
def email = issue.getCustomFieldValue(emailField)
def vrstaLicne = issue.getCustomFieldValue(vrstaLicneField)
def brojLicne = issue.getCustomFieldValue(brojLicneField)
def izdataU = issue.getCustomFieldValue(izdataUField)
def ocevoIme = issue.getCustomFieldValue(ocevoImeField)
def kategorija = issue.getCustomFieldValue(kategorijaField)
def datumLjekarskog = issue.getCustomFieldValue(datumLjekarskogField)
def brojLjekarskog = issue.getCustomFieldValue(brojLjekarskogField)
def slobodnoVrijemeVoznja = issue.getCustomFieldValue(slobodnoVrijemeVoznjaField)
def slobodnoVrijemeTeorija = issue.getCustomFieldValue(slobodnoVrijemeTeorijaField)
def zeljeniInstruktor = issue.getCustomFieldValue(zeljeniInstruktorField)
def datumIstekaLjekarskog = issue.getCustomFieldValue(datumIstekaLjekarskogField)
def postanskiBroj = issue.getCustomFieldValue(postanskiBrojField)
def adresa = issue.getCustomFieldValue(adresaField)
def zanimanje = issue.getCustomFieldValue(zanimanjeField)
def drzava = issue.getCustomFieldValue(drzavaField)
def drzavljanstvo = issue.getCustomFieldValue(drzavljanstvoField)
def mjestoRodjenja = issue.getCustomFieldValue(mjestoRodjenjaField)
def pol = issue.getCustomFieldValue(polField)
def datumRodjenja = issue.getCustomFieldValue(datumRodjenjaField)
def jmbg = issue.getCustomFieldValue(jmbgField)
def datum = issue.getCustomFieldValue(datumField)

		MutableIssue kandidatIssue = issueFactory.getIssue()
		kandidatIssue.setSummary("$ime | $prezime | $telefon")
		kandidatIssue.setProjectObject(KAN)
		kandidatIssue.setIssueTypeId("10200")
		kandidatIssue.setCustomFieldValue(imeField, ime)
		kandidatIssue.setCustomFieldValue(prezimeField, prezime)
		kandidatIssue.setCustomFieldValue(telefonField, telefon)
		kandidatIssue.setCustomFieldValue(viberField, viber)
		kandidatIssue.setCustomFieldValue(emailField, email)
		kandidatIssue.setCustomFieldValue(vrstaLicneField, vrstaLicne)
		kandidatIssue.setCustomFieldValue(brojLicneField, brojLicne)
		kandidatIssue.setCustomFieldValue(izdataUField, izdataU)
		kandidatIssue.setCustomFieldValue(ocevoImeField, ocevoIme)
		kandidatIssue.setCustomFieldValue(kategorijaField, kategorija)
		kandidatIssue.setCustomFieldValue(datumLjekarskogField, datumLjekarskog)
		kandidatIssue.setCustomFieldValue(brojLjekarskogField, brojLjekarskog)
		kandidatIssue.setCustomFieldValue(datumField, datum)

		kandidatIssue.setCustomFieldValue(slobodnoVrijemeVoznjaField, slobodnoVrijemeVoznja)
		kandidatIssue.setCustomFieldValue(slobodnoVrijemeTeorijaField, slobodnoVrijemeTeorija)
		kandidatIssue.setCustomFieldValue(zeljeniInstruktorField, zeljeniInstruktor)
		kandidatIssue.setCustomFieldValue(datumIstekaLjekarskogField, datumIstekaLjekarskog)
		kandidatIssue.setCustomFieldValue(postanskiBrojField, postanskiBroj)
		kandidatIssue.setCustomFieldValue(adresaField, adresa)
		kandidatIssue.setCustomFieldValue(zanimanjeField, zanimanje)
		kandidatIssue.setCustomFieldValue(drzavaField, drzava)
		kandidatIssue.setCustomFieldValue(drzavljanstvoField, drzavljanstvo)
		kandidatIssue.setCustomFieldValue(polField, pol)
		kandidatIssue.setCustomFieldValue(mjestoRodjenjaField, mjestoRodjenja)
		kandidatIssue.setCustomFieldValue(datumRodjenjaField, datumRodjenja)
		kandidatIssue.setCustomFieldValue(jmbgField, jmbg)

		kandidatIssue.setAssignee(currentUser)
		kandidatIssue.setReporter(currentUser)
		Map<String,Object> kandidatIssueParams = ["issue" : kandidatIssue] as Map<String,Object>
		issueManager.createIssueObject(visol, kandidatIssueParams)

