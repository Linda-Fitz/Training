import com.atlassian.jira.issue.Issue
import java.lang.Thread
import org.ofbiz.core.entity.GenericValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.bc.issue.IssueService
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.CasObuke")
log.setLevel(Level.DEBUG)

if (issue.getIssueTypeId() != "10601") { return }

def issueManager = ComponentAccessor.getIssueManager()
IssueService issueService = ComponentAccessor.getIssueService()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3068")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def ocevoField = cfManager.getCustomFieldObject("customfield_10117")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def telefonField = cfManager.getCustomFieldObject("customfield_10220")
def ime = issue.getCustomFieldValue(imeField)
def ocevo = issue.getCustomFieldValue(ocevoField)
def prezime = issue.getCustomFieldValue(prezimeField)
def telefon = issue.getCustomFieldValue(telefonField)

def posaljizahtjevField = cfManager.getCustomFieldObject("customfield_10922")
def posaljiZahtjev = issue.getCustomFieldValue(posaljizahtjevField)
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

MutableIssue transitionIssue = (MutableIssue)issue
def sum = "Dodatni cas | $ime $prezime | $telefon"   
transitionIssue.setSummary(sum) 
transitionIssue.setAssignee(currentUser)
transitionIssue.setReporter(currentUser)

def parentIssue = issue.getParentObject() 
def zeljenoTrajanjeField = cfManager.getCustomFieldObject("customfield_10918")
def zakazanoTrajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def ugovorenaCijenaField = cfManager.getCustomFieldObject("customfield_10923")
def casField = cfManager.getCustomFieldObject("customfield_10502")
def zaPlatitiField = cfManager.getCustomFieldObject("customfield_11303")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
if (issue.getIssueTypeId() != "10601") { return }

def cijenaCasa = 0
if (parentIssue.getCustomFieldValue(ugovorenaCijenaField)) {
	cijenaCasa = parentIssue.getCustomFieldValue(ugovorenaCijenaField).toDouble() / parentIssue.getCustomFieldValue(zeljenoTrajanjeField).toDouble() * issue.getCustomFieldValue(zakazanoTrajanjeField).toDouble()
} else {
	cijenaCasa = parentIssue.getCustomFieldValue(casField).toDouble() / parentIssue.getCustomFieldValue(zeljenoTrajanjeField).toDouble() * issue.getCustomFieldValue(zakazanoTrajanjeField).toDouble()	
	def razlika = parentIssue.getCustomFieldValue(casField).toDouble() - cijenaCasa
	def preostalo = parentIssue.getCustomFieldValue(preostaloField).toDouble() - razlika
	def zaPlatiti = parentIssue.getCustomFieldValue(zaPlatitiField).toDouble() - razlika
	zaPlatitiField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(zaPlatitiField), zaPlatiti.round(2)),changeHolder)
	preostaloField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(preostaloField), preostalo.round(2)),changeHolder)
}

zaPlatitiField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zaPlatitiField), cijenaCasa.round(2)),changeHolder)

if(posaljiZahtjev.toString() == "DA" && issue.getStatusId() == "10501"){ // Zakazano
	Thread.start {
		sleep(2000)
		
		WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
		workflowTransitionUtil.setAction(31); 
		workflowTransitionUtil.setUserkey("visol");
		workflowTransitionUtil.setIssue(transitionIssue);    
		workflowTransitionUtil.validate();
		workflowTransitionUtil.progress();
		
		int actionId = 31
		def transitionResult
		def transitionValidationResult = issueService.validateTransition(visol, transitionIssue.id, actionId, new IssueInputParametersImpl())
		if (transitionValidationResult.isValid()) {
			transitionResult = issueService.transition(visol, transitionValidationResult)
			if (transitionResult.isValid()) { 
				log.debug("Transitioned issue $issue through action $actionId") 
			} else { 
				log.debug("Transition result is not valid") 			
			}
		} else {
			log.debug("The transitionValidation is not valid")
			def errors = transitionValidationResult.getErrorCollection().getErrorMessages()
			log.debug(errors.getClass())
			def warnings = transitionValidationResult.getWarningCollection().getWarnings()
			errors.each {
				log.debug(it)
			}
			log.debug(warnings)
			warnings.each {
				log.debug(it)
			}
		}
				
	}	
	sleep(2000)
}   
 