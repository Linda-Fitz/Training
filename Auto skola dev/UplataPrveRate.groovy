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
 
def log = Logger.getLogger("com.acme.UplataPrveRate")
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

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-4242")

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
def ukupnoZaPlatitiField = cfManager.getCustomFieldObject("customfield_10316")

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
def ukupnoZaPlatiti = issue.getCustomFieldValue(ukupnoZaPlatitiField)

double x = 0

//if (!iznosUplate) { return }

def ukupnoUplate = (issue.getCustomFieldValue(ukupnoUplateField)) ? issue.getCustomFieldValue(ukupnoUplateField) + iznosUplate : iznosUplate
ukupnoUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(ukupnoUplateField), iznosUplate),changeHolder)
def preostaloUplate = (issue.getCustomFieldValue(preostaloField)) ? issue.getCustomFieldValue(preostaloField) : iznosUplate  //- iznosUplate
preostaloUplate = (preostaloUplate < 0) ? (double)0 : preostaloUplate 
log.debug("Preostalo pre petlje: " + preostaloUplate)
log.debug("Ukupno uplate pre petlje: " + ukupnoUplate)
log.debug("Za platiti pre petlje: " + ukupnoZaPlatiti)

	if (ukupnoUplate < ukupnoZaPlatiti){
				preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostaloUplate),changeHolder)
				log.debug("preostalo: " + issue.getCustomFieldValue(preostaloField))
			}else{
				preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), x),changeHolder)
				log.debug("preostalo0: " + issue.getCustomFieldValue(preostaloField))
			}

//preostaloField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(preostaloField), preostaloUplate),changeHolder)

//ukupnoUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(ukupnoUplateField), ukupnoUplate),changeHolder)
//iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
//datumUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUplateField), null),changeHolder)
def specUplataUpdate = (specUplata) ? specUplata + ("\n" + sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName") : sadStr + " - \u20ac " + f.format(iznosUplate) + " $currentUserName" 
specUplataUpdate = specUplataUpdate.toString()
log.debug(specUplataUpdate)
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
iznosUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(iznosUplateField), null),changeHolder)
datumUplateField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumUplateField), null),changeHolder)