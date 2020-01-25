import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl;
import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import java.text.SimpleDateFormat
import java.sql.Timestamp
import java.util.Date
import java.text.DateFormat

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-681")
def parentIssue = issue.getParentObject() 
UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

Date now = new Date(System.currentTimeMillis())
def nowFormatted = new SimpleDateFormat("dd/MM/yyyy").format(now).toString()

def subtasks = parentIssue.getSubTaskObjects()
	def imeField = cfManager.getCustomFieldObject("customfield_10132")
	def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
	def ime = issue.getCustomFieldValue(imeField)
	if(!ime){
		ime = ""
	}
	def prezime = issue.getCustomFieldValue(prezimeField)
	if(!prezime){
		prezime = ""
	}
    def brojField = cfManager.getCustomFieldObject("customfield_10217")
	def broj = issue.getCustomFieldValue(brojField)
	if(!broj){
		broj = ""
	}
	
if (!subtasks){
    return
}
def datumRaskidaUgovoraField=cfManager.getCustomFieldObject("customfield_11604") 
def datumRaskidaUgovora = issue.getCustomFieldValue(datumRaskidaUgovoraField)
def sad = new Timestamp(System.currentTimeMillis())


subtasks.each {subtask ->
	if(subtask.getStatusId() == "10100"){
		datumRaskidaUgovoraField.updateValue(null, subtask, new ModifiedValue(datumRaskidaUgovora, sad),changeHolder)	
 	 	workflowTransitionUtil.setIssue(subtask);
     	workflowTransitionUtil.setAction(21);
     	workflowTransitionUtil.validate(); 
     	workflowTransitionUtil.progress();
    }
   
}
