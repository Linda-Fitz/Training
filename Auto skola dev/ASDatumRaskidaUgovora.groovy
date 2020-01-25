import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.Issue
import java.text.SimpleDateFormat 
import java.sql.Timestamp
import java.util.Date
import org.apache.log4j.Level
import org.apache.log4j.Logger

log = Logger.getLogger("com.acme.SetovanjePolja")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def userUtil = ComponentAccessor.getUserUtil()
def visol = userUtil.getUserByName("VISOL")
def changeHolder = new DefaultIssueChangeHolder()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-1008")

def datumRaskidaUgovoraField=cfManager.getCustomFieldObject("customfield_11604")
def datumRaskidaUgovora = issue.getCustomFieldValue(datumRaskidaUgovoraField)

def sad = new Timestamp(System.currentTimeMillis())
def subTasksList = issue.getSubTaskObjects()

datumRaskidaUgovoraField.updateValue(null, issue, new ModifiedValue(datumRaskidaUgovora, sad),changeHolder)	

subTasksList.each{
	if(it.getStatusId() == "10100"){
		log.debug(it)
		datumRaskidaUgovoraField.updateValue(null, it, new ModifiedValue(datumRaskidaUgovora, sad),changeHolder)	
	}
}
log.debug(issue.getCustomFieldValue(datumRaskidaUgovoraField))