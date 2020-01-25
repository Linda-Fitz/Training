import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils;
import java.text.SimpleDateFormat
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.CasTeorijeDatumZavrsetka")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def changeHolder = new DefaultIssueChangeHolder()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

def datumZavrsetkaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10630")
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-265")

def datumField = cfManager.getCustomFieldObject("customfield_10624")
def prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10618") 
def prisutniKandidati = issue.getCustomFieldValue(prisutniKandidatiField)

log.debug("Prisutni kandidati: " + prisutniKandidati)


prisutniKandidati.each { kandidat ->
	if (kandidat.getStatusId() == "10100" || kandidat.getStatusId() == "10201") {
		workflowTransitionUtil.setAction(121) // obuka zavrsen 
		workflowTransitionUtil.setIssue(kandidat)
		workflowTransitionUtil.validate()
		workflowTransitionUtil.progress()			
	}
}

def teorijaIssue
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10500) {
		teorijaIssue = link.getSourceObject()
		break
	}
}

if (!teorijaIssue) { return }

def subtasks = []

for (IssueLink linkBack in linkMgr.getOutwardLinks(teorijaIssue.id)) {
	if (linkBack.getLinkTypeId() == 10500) {
		def grupaIssue = linkBack.getDestinationObject()
		if (grupaIssue.getStatusId() != "10301") {
			return
		} else {
			grupaIssue.getSubTaskObjects().each {
				subtasks << it
			}
		}
	}	
}

subtasks.sort{ a, b ->
	a.getCustomFieldValue(datumField) <=> b.getCustomFieldValue(datumField)
}

if (subtasks) {
	def datumStr = subtasks.last().getCustomFieldValue(datumField).toString().split(" ")[0]
	def date = new Date().parse("yyyy-MM-dd", datumStr)
	datumZavrsetkaField.updateValue(null, teorijaIssue, new ModifiedValue(teorijaIssue.getCustomFieldValue(datumZavrsetkaField), date.toTimestamp()),changeHolder)
}

if (teorijaIssue.getStatusId() == "10311") {
	workflowTransitionUtil.setAction(81) // zavrsena 
	workflowTransitionUtil.setIssue(teorijaIssue)
	workflowTransitionUtil.validate()
	workflowTransitionUtil.progress()	
}
