import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.OdobriGratis")
log.setLevel(Level.DEBUG)

def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()

def casField = cfManager.getCustomFieldObject("customfield_10502")
def zaPlatitiField = cfManager.getCustomFieldObject("customfield_11256")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")

def zp = issue.getCustomFieldValue(zaPlatitiField)
if (!zp || zp == 0) { return }

double par
par = 0

def zaPlatiti = parent.getCustomFieldValue(zaPlatitiField).toDouble()
def preostalo = parent.getCustomFieldValue(preostaloField).toDouble()
zaPlatitiField.updateValue(null, issue, new ModifiedValue(parent.getCustomFieldValue(zaPlatitiField), par ),changeHolder)
//preostaloField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(preostaloField), (preostalo - zp.toDouble()).round(2)),changeHolder)