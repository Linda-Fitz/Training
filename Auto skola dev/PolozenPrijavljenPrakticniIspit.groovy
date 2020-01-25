import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import org.apache.log4j.Logger
import org.apache.log4j.Level
 import com.atlassian.jira.issue.MutableIssue;

def log = Logger.getLogger("com.acme.PolozioPrakticniIspit")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-3854")

def datumIVrijemePolaganjaPoligonField = cfManager.getCustomFieldObject("customfield_11260")
def datumIVrijemePolaganjaGradskaField = cfManager.getCustomFieldObject("customfield_11261")
def datumPolozenogPoligonField = cfManager.getCustomFieldObject("customfield_11267")
def datumPolozenogGradskaField = cfManager.getCustomFieldObject("customfield_11268")
def brojPolaganjaField = cfManager.getCustomFieldObject("customfield_11266")


def parent = (MutableIssue) issue.getParentObject()
def ispit = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11265"))

def datumIVrijemePolaganjaPoligon = ispit.getCustomFieldValue(datumIVrijemePolaganjaPoligonField)
def datumIVrijemePolaganjaGradska = ispit.getCustomFieldValue(datumIVrijemePolaganjaGradskaField)
def brojPolaganja = issue.getCustomFieldValue(brojPolaganjaField)
def uplacenIspitPrakticni = cfManager.getCustomFieldObject("customfield_11263")

log.debug(datumIVrijemePolaganjaPoligon)
log.debug(datumIVrijemePolaganjaGradska)
(!datumIVrijemePolaganjaPoligon)? datumPolozenogPoligonField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPolozenogPoligonField), datumIVrijemePolaganjaPoligon), changeHolder): null
	
datumPolozenogGradskaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPolozenogGradskaField), datumIVrijemePolaganjaGradska), changeHolder)

(!datumIVrijemePolaganjaPoligon)? datumPolozenogPoligonField.updateValue(null, parent, new ModifiedValue(issue.getCustomFieldValue(datumPolozenogPoligonField), datumIVrijemePolaganjaPoligon), changeHolder): null

datumPolozenogGradskaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(datumPolozenogGradskaField), datumIVrijemePolaganjaGradska), changeHolder)
brojPolaganjaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojPolaganjaField), brojPolaganja), changeHolder)

if (parent.getStatusId() == "10307") { //voznja ispit prijavljen
	WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class);
	workflowTransitionUtil.setUserkey("visol")
	workflowTransitionUtil.setAction(71) //spreman za svedocanstvo
	workflowTransitionUtil.setIssue(parent)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
	
	def fieldConfig = uplacenIspitPrakticni.getRelevantConfig(parent)
	def da = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == 'DA'}
	uplacenIspitPrakticni.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(uplacenIspitPrakticni), da ),changeHolder)
	
}