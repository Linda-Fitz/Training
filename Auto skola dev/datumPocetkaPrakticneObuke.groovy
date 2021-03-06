import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
log = Logger.getLogger("com.acme.datumPocetkaObuke")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
changeHolder = new DefaultIssueChangeHolder()
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

sad = new Timestamp(System.currentTimeMillis())

def datumPocetkaObukeField = cfManager.getCustomFieldObject("customfield_12311")
def datumPocetka = issue.getCustomFieldValue(datumPocetkaObukeField)
if(!datumPocetka){
datumPocetkaObukeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(datumPocetkaObukeField), sad),changeHolder)	
}




