import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import org.apache.log4j.Level
import org.apache.log4j.Logger

log = Logger.getLogger("com.acme.test")
log.setLevel(Level.DEBUG)

//Issue issue = event?.getIssue()
//log.debug(event.getEventTypeId().toString())
issueTypeId = issue.getIssueTypeId()
if (issueTypeId != "10201" && issueTypeId != "10103" && issueTypeId != "10102" && issueTypeId != "10306") { return }

cfManager = ComponentAccessor.getCustomFieldManager()
changeHolder = new DefaultIssueChangeHolder()
//issue = issueManager.getIssueByKeyIgnoreCase("CE-827")

def predjenoKilometaraField = cfManager.getCustomFieldObject("customfield_10513")
def kilometrazaField = cfManager.getCustomFieldObject("customfield_10311")
def datumField = cfManager.getCustomFieldObject("customfield_10315")
def iznosField = cfManager.getCustomFieldObject("customfield_10316")
def utocenoField = cfManager.getCustomFieldObject("customfield_10317")
def prosjecnoField = cfManager.getCustomFieldObject("customfield_10524")

parentIssue = issue.getParentObject()
log.debug("Parent: ${parentIssue.key}")
subtasks = parentIssue.getSubTaskObjects()
log.debug("Subtatk: ${subtasks[0]}")

def sameTypeSubtasks = subtasks.findAll { it.getIssueTypeId() == issueTypeId }

//if (event.getEventTypeId() != 8) { sameTypeSubtasks << issue }
if (sameTypeSubtasks.size() == 0) { return }

sameTypeSubtasks.sort { a, b -> a.getCustomFieldValue(kilometrazaField) <=> b.getCustomFieldValue(kilometrazaField) }

for (int i = 1; i < sameTypeSubtasks.size(); i++) {
    def subtask = sameTypeSubtasks[i]
    def razlika = 1
    if (sameTypeSubtasks.size() > 1) {
        def previousSubtask = sameTypeSubtasks[i - 1]
        def previousKilometraza = (previousSubtask.getCustomFieldValue(kilometrazaField)) ? previousSubtask.getCustomFieldValue(kilometrazaField).toDouble() : 0
        razlika = subtask.getCustomFieldValue(kilometrazaField)?.toDouble() - previousKilometraza
        predjenoKilometaraField.updateValue(null, subtask, new ModifiedValue(subtask.getCustomFieldValue(predjenoKilometaraField), razlika),changeHolder)
        if (issueTypeId == "10102") { // gorivo - utoceno
            def utoceno = subtask.getCustomFieldValue(utocenoField)
			log.debug(utoceno)
           prosjecnoField.updateValue(null, subtask, new ModifiedValue(subtask.getCustomFieldValue(predjenoKilometaraField),  utoceno / razlika * 100),changeHolder) 
        }
    }
}

if (issueTypeId == "10201") { // Kvarovi
    def brojKvarovaField = cfManager.getCustomFieldObject("customfield_11243")
    def trosakKvarovaField = cfManager.getCustomFieldObject("customfield_11247")
    def prosjecnoPredjenihKilometaraKField = cfManager.getCustomFieldObject("customfield_11253")

    def brojKvarova = sameTypeSubtasks.size()
    def trosakKvarova = 0
    def prosjecnoPredjenihKilometaraK = 0
    sameTypeSubtasks.each {
        trosakKvarova += (it.getCustomFieldValue(iznosField)) ? it.getCustomFieldValue(iznosField) : 0
    }
  
    if (sameTypeSubtasks.size() > 1) {
        def prevKilometraza = (sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField)) ? sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField).toDouble() : 0 
        def predjenoKilometara = sameTypeSubtasks[sameTypeSubtasks.size() - 1].getCustomFieldValue(kilometrazaField) - prevKilometraza
        if (predjenoKilometara > 0) { prosjecnoPredjenihKilometaraK = predjenoKilometara / (brojKvarova - 1) } 
    }

    brojKvarovaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(brojKvarovaField), (double)brojKvarova), changeHolder)
    trosakKvarovaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(trosakKvarovaField), (double)trosakKvarova), changeHolder)
    prosjecnoPredjenihKilometaraKField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prosjecnoPredjenihKilometaraKField), (double)prosjecnoPredjenihKilometaraK), changeHolder)
} else if (issueTypeId == "10103") { // Gume
    def brojZamjenaGumaField = cfManager.getCustomFieldObject("customfield_11245")
    def trosakGumaField = cfManager.getCustomFieldObject("customfield_11248")
    def prosjecnoPredjenihKilometaraGField = cfManager.getCustomFieldObject("customfield_11252")

    def brojZamjenaGuma = sameTypeSubtasks.size()
    def trosakGuma = 0
    def prosjecnoPredjenihKilometaraG = 0
    sameTypeSubtasks.each {
        trosakGuma += it.getCustomFieldValue(iznosField) ?: 0
        prosjecnoPredjenihKilometaraG += it.getCustomFieldValue(predjenoKilometaraField) ?: 0
    }

    if (sameTypeSubtasks.size() > 1) {
        def prevKilometraza = (sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField)) ? sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField).toDouble() : 0 
        def predjenoKilometara = sameTypeSubtasks[sameTypeSubtasks.size() - 1].getCustomFieldValue(kilometrazaField) - prevKilometraza
        if (predjenoKilometara > 0) { prosjecnoPredjenihKilometaraG = predjenoKilometara / (brojZamjenaGuma - 1) } 
    }

    brojZamjenaGumaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(brojZamjenaGumaField), (double)brojZamjenaGuma), changeHolder)
    trosakGumaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(trosakGumaField), (double)trosakGuma), changeHolder)
    prosjecnoPredjenihKilometaraGField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prosjecnoPredjenihKilometaraGField), (double)prosjecnoPredjenihKilometaraG), changeHolder)
} else if (issueTypeId == "10102") { // Gorivo
    def utocenoLitaraLField = cfManager.getCustomFieldObject("customfield_10317")
    def potrosenoLitaraLField = cfManager.getCustomFieldObject("customfield_11250")
    def trosakGorivaField = cfManager.getCustomFieldObject("customfield_11249")
    def predjenoKilometaraGField = cfManager.getCustomFieldObject("customfield_10513")
    def prosjecnaPotrosnjaField = cfManager.getCustomFieldObject("customfield_10524")
    def prosjecnaCijenaGorivaField = cfManager.getCustomFieldObject("customfield_11254")

    def utocenoLitaraL = 0
    def trosakGoriva = 0
    def poslednjeTocenje = 0
    def poslednjiTrosak = 0
    sameTypeSubtasks.eachWithIndex { it, i ->
        if (i == sameTypeSubtasks.size() - 1) { 
            poslednjeTocenje = it.getCustomFieldValue(utocenoField) ?: 0
            poslednjiTrosak = it.getCustomFieldValue(iznosField) ?: 0 
        } else {
            utocenoLitaraL += it.getCustomFieldValue(utocenoField) ?: 0
			log.debug(utocenoLitaraL)
            trosakGoriva += it.getCustomFieldValue(iznosField) ?: 0 
			log.debug(trosakGoriva)
			//trosakGoriva = trosakGoriva / 2
        }        
    }
    def predjenoKilometaraG = 0
    def prosjecnaPotrosnja = 0
    if (sameTypeSubtasks.size() > 1) {
        def prevKilometraza = (sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField)) ? sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField).toDouble() : 0 
        predjenoKilometaraG = sameTypeSubtasks[sameTypeSubtasks.size() - 1].getCustomFieldValue(kilometrazaField) - prevKilometraza
        prosjecnaPotrosnja = (predjenoKilometaraG > 0) ? utocenoLitaraL / predjenoKilometaraG * 100 : 0
    }

    def prosjecnaCijenaGoriva = 0
	def potrosenoLitaraL = utocenoLitaraL
    utocenoLitaraL = utocenoLitaraL + poslednjeTocenje
    trosakGoriva = trosakGoriva + poslednjiTrosak
    if (trosakGoriva > 0 && utocenoLitaraL > 0) {
        prosjecnaCijenaGoriva = (trosakGoriva + poslednjiTrosak) / (utocenoLitaraL + poslednjeTocenje)
    } 

    

    utocenoLitaraLField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(utocenoLitaraLField), (double)utocenoLitaraL), changeHolder)
    potrosenoLitaraLField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(potrosenoLitaraLField), (double)utocenoLitaraL), changeHolder)
    trosakGorivaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(trosakGorivaField), (double)trosakGoriva), changeHolder)
    predjenoKilometaraGField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(predjenoKilometaraGField), (double)predjenoKilometaraG), changeHolder)
   prosjecnaPotrosnjaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prosjecnaPotrosnjaField), (double)prosjecnaPotrosnja), changeHolder)
    prosjecnaCijenaGorivaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prosjecnaCijenaGorivaField), (double)prosjecnaCijenaGoriva), changeHolder) 
} else if (issueTypeId == "10306") { // Servis
    def brojServisaField = cfManager.getCustomFieldObject("customfield_11244")
    def trosakServisaField = cfManager.getCustomFieldObject("customfield_11246")
    def prosjecnoPredjenihKilometaraSField = cfManager.getCustomFieldObject("customfield_11251")

    def brojServisa = sameTypeSubtasks.size()
	log.debug(brojServisa)
    def trosakServisa = 0
    def prosjecnoPredjenihKilometaraS = 0
    sameTypeSubtasks.each {
		trosakServisa += it.getCustomFieldValue(iznosField) 

        
    }

    if (sameTypeSubtasks.size() > 1) {
        def prevKilometraza = (sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField)) ? sameTypeSubtasks[0].getCustomFieldValue(kilometrazaField).toDouble() : 0 
        def predjenoKilometara = sameTypeSubtasks[sameTypeSubtasks.size() - 1].getCustomFieldValue(kilometrazaField) - prevKilometraza
        if (predjenoKilometara > 0) { prosjecnoPredjenihKilometaraS = predjenoKilometara / (brojServisa - 1) } 
    }

    brojServisaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(brojServisaField), (double)brojServisa), changeHolder)
    trosakServisaField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(trosakServisaField), (double)trosakServisa), changeHolder)
    prosjecnoPredjenihKilometaraSField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prosjecnoPredjenihKilometaraSField), (double)prosjecnoPredjenihKilometaraS), changeHolder)
}