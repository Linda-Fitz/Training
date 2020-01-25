import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.Uplata")
log.setLevel(Level.DEBUG)

DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALY);
f = new DecimalFormat("#,###.00", symbols)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def currentUserName = currentUser.getDisplayName()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-6474")
def ukupnoZaPlatitiField = cfManager.getCustomFieldObject("customfield_11302")
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
def specUplataField = cfManager.getCustomFieldObject("customfield_10804")

def ukupnoZaPlatiti = issue.getCustomFieldValue(ukupnoZaPlatitiField)
def datumUplate = issue.getCustomFieldValue(datumUplateField)
def iznosUplate = issue.getCustomFieldValue(iznosUplateField)
def ime = issue.getCustomFieldValue(imeField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)
def viber = issue.getCustomFieldValue(viberField)
def email = issue.getCustomFieldValue(emailField)
def iznosRate = issue.getCustomFieldValue(iznosRateField)
def specUplata = issue.getCustomFieldValue(specUplataField) 
def sad = new Timestamp(System.currentTimeMillis())
def sadStr = formatDatum(sad)

def vrstaPlacanjaField = cfManager.getCustomFieldObject("customfield_10320")
def vrstaPlacanja = issue.getCustomFieldValue(vrstaPlacanjaField)
log.debug("Vrsta placanja: "+ vrstaPlacanja)

def specUplataUpdate
def ukupnoUplate
def preostaloUplate 

def prekoraceniIznosField = cfManager.getCustomFieldObject("customfield_10819")
def prekoracenje = issue.getCustomFieldValue(prekoraceniIznosField)
def vecprekoraceniIznos
def prekoraceniIznosTotal
def val
//prekoraceniIznosField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prekoraceniIznosField), "0"),changeHolder)

if (vrstaPlacanja == "Jednokratno" || vrstaPlacanja == "Jednokratno- Premium kartica") {
			if (!iznosUplate) { return }
				log.debug("iznosUplate1: " + iznosUplate)

			specUplataUpdate = (specUplata) ? specUplata + ("\n" + sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName") : sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName" 
			specUplataUpdate = specUplataUpdate.toString()
			ukupnoUplate = (issue.getCustomFieldValue(ukupnoUplateField)) ? issue.getCustomFieldValue(ukupnoUplateField) + iznosUplate : iznosUplate
			ukupnoUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(ukupnoUplateField), ukupnoUplate),changeHolder)
			log.debug("ukupnoUplate: " + ukupnoUplate)
			preostaloUplate = (issue.getCustomFieldValue(preostaloField)) ? ukupnoZaPlatiti - ukupnoUplate : iznosUplate
			preostaloUplate = (preostaloUplate < 0) ? (double)0 : preostaloUplate  
			preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostaloUplate),changeHolder)
			log.debug("preostalo: " + issue.getCustomFieldValue(preostaloField))
			iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
			//log.debug("iznosUplate: "+issue.getCustomFieldValue(iznosUplateField))
			datumUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUplateField), null),changeHolder)
			specUplataField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(specUplataField), specUplataUpdate),changeHolder)
			val = iznosUplate - issue.getCustomFieldValue(preostaloField)
			log.debug("Val"+val)
			if (val > 0 ){
				prekoraceniIznosField.updateValue(null, issue, new ModifiedValue(prekoracenje, val),changeHolder)
				log.debug("prekoracenje: "+ issue.getCustomFieldValue(prekoraceniIznosField))
			}else{
				prekoraceniIznosField.updateValue(null, issue, new ModifiedValue(prekoracenje, 0),changeHolder)
				log.debug("prekoracenje0: "+ issue.getCustomFieldValue(prekoraceniIznosField))
			}
			
	}else{ //log.debug(iznosUplate)
			if (!iznosUplate) { return }

			specUplataUpdate = (specUplata) ? specUplata + ("\n" + sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName") : sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName" 
			specUplataUpdate = specUplataUpdate.toString()
			ukupnoUplate = (issue.getCustomFieldValue(ukupnoUplateField)) ? issue.getCustomFieldValue(ukupnoUplateField) + iznosUplate : iznosUplate
			ukupnoUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(ukupnoUplateField), ukupnoUplate),changeHolder)
			log.debug("ukupnoUplate na rate" + ukupnoUplate)
			preostaloUplate = (issue.getCustomFieldValue(preostaloField)) ? issue.getCustomFieldValue(preostaloField) - iznosUplate : iznosUplate
			preostaloUplate = (preostaloUplate < 0) ? (double)0 : preostaloUplate  
			preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostaloUplate),changeHolder)
			iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
			datumUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUplateField), null),changeHolder)
			specUplataField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(specUplataField), specUplataUpdate),changeHolder)
			preostaliIznos = iznosUplate
			def subtasks = issue.getSubTaskObjects()
			// Sort po datumu
			
			subtasks.sort{a, b ->
				a.getCustomFieldValue(datumDospjecaField) <=> b.getCustomFieldValue(datumDospjecaField)
			}
			subtasks.each { rata ->
				if (rata.getStatusId() == "10403" ) {
					return
				}else if(rata.getIssueTypeId() == "10303") {
				MutableIssue ratoza = rata

				if (preostaliIznos > 0) {
					def datumDospjecaUplate = ratoza.getCustomFieldValue(datumDospjecaField)
					def datumstr = new Date(datumDospjecaUplate.getTime()).format("dd.MM.yyyy")
					def uplacenoNaRati = (ratoza.getCustomFieldValue(iznosUplateField)) ? ratoza.getCustomFieldValue(iznosUplateField) : (double)0 
					def novouplaceno = uplacenoNaRati + preostaliIznos
					def sum = ratoza.summary
					sum = sum.substring(0, sum.size() - sum.split("\u20ac")[-1].size() + 1)
					if (iznosRate > novouplaceno) {
						sum = sum + f.format(novouplaceno) + "/" + f.format(iznosRate)
						ratoza.setSummary(sum)
						issueManager.updateIssue(visol, ratoza, EventDispatchOption.ISSUE_UPDATED, false)
						iznosUplateField.updateValue(null, ratoza, new ModifiedValue(ratoza.getCustomFieldValue(iznosUplateField), uplacenoNaRati + preostaliIznos),changeHolder)
						preostaloField.updateValue(null, ratoza, new ModifiedValue(ratoza.getCustomFieldValue(preostaloField), iznosRate - uplacenoNaRati - preostaliIznos),changeHolder)
						preostaliIznos = 0
					} else {
						preostaliIznos = preostaliIznos - iznosRate + uplacenoNaRati
						sum = sum + f.format(iznosRate) + "/" + f.format(iznosRate)
						ratoza.setSummary(sum)
						issueManager.updateIssue(visol, ratoza, EventDispatchOption.ISSUE_UPDATED, false)
						iznosUplateField.updateValue(null, ratoza, new ModifiedValue(ratoza.getCustomFieldValue(iznosUplateField), iznosRate),changeHolder)
						preostaloField.updateValue(null, ratoza, new ModifiedValue(ratoza.getCustomFieldValue(preostaloField), (double)0),changeHolder)
						if (ratoza.getStatusId() == "10102") {  // Otvoreno
							workflowTransitionUtil.setAction (31)
			 				workflowTransitionUtil.setIssue(ratoza);
			    			workflowTransitionUtil.validate();
			    			workflowTransitionUtil.progress();
						} else if (ratoza.getStatusId() == "10402") {
							workflowTransitionUtil.setAction (21)
			 				workflowTransitionUtil.setIssue(ratoza);
			    			workflowTransitionUtil.validate();
			    			workflowTransitionUtil.progress();
						}
					}	
				}
				val = iznosUplate - issue.getCustomFieldValue(preostaloField)
				if (val > 0) {
					vecprekoraceniIznos = (issue.getCustomFieldValue(prekoraceniIznosField)) ? issue.getCustomFieldValue(prekoraceniIznosField) : (double)0
					prekoraceniIznosTotal = vecprekoraceniIznos + val
					prekoraceniIznosField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(prekoraceniIznosField), prekoraceniIznosTotal),changeHolder)
				}
			}	 
		}
}
def formatDatum(Timestamp datumIspita) {
				def datumIspitaStr = datumIspita.toString()
				def year = datumIspitaStr.split("-")[0]
				def month = datumIspitaStr.split("-")[1]
				def day = datumIspitaStr.split("-")[2].split(" ")[0]
				def hour = datumIspitaStr.split(" ")[1].split(":")[0]
				def minute = datumIspitaStr.split(" ")[1].split(":")[1]

				return "$day-$month-$year $hour:$minute"
			}