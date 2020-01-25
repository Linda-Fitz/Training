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
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.CasObuke")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
IssueService issueService = ComponentAccessor.getIssueService()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3112")
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def parentIssue = issue.getParentObject() 
def zeljenoTrajanjeField = cfManager.getCustomFieldObject("customfield_10918")
def zakazanoTrajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def ugovorenaCijenaField = cfManager.getCustomFieldObject("customfield_10923")
def casField = cfManager.getCustomFieldObject("customfield_10502")
def zaPlatitiField = cfManager.getCustomFieldObject("customfield_11303")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
def ugovorenIznosField = cfManager.getCustomFieldObject("customfield_11269")
def gratisField = cfManager.getCustomFieldObject("customfield_10922")
def posaljiZahtjev = issue.getCustomFieldValue(gratisField).toString()

    def ugovorenIznos = parentIssue.getCustomFieldValue(ugovorenIznosField)
    def zaPlatiti = parentIssue.getCustomFieldValue(zaPlatitiField).toDouble()  
	cijenaCasa = parentIssue.getCustomFieldValue(ugovorenaCijenaField).toDouble() / parentIssue.getCustomFieldValue(zeljenoTrajanjeField).toDouble() * issue.getCustomFieldValue(zakazanoTrajanjeField).toDouble()
    def ukupno = zaPlatiti + cijenaCasa
    if(ukupno > ugovorenIznos && posaljiZahtjev != "DA"){
      invalidInputException = new InvalidInputException("Ne moze se zakazati cas jer je prekoracen ugovoren iznos uplate!") 
}


 