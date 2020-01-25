import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZeljeniIstruktor")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def issueManager = ComponentAccessor.getIssueManager()

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7595")
def zeljeniInstruktorField = cfManager.getCustomFieldObject("customfield_10318")
def imeField = cfManager.getCustomFieldObject("customfield_10132")
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def imeInstruktoraField = cfManager.getCustomFieldObject("customfield_12001")

def zeljeniInstruktor = issue.getCustomFieldValue(zeljeniInstruktorField)
zeljeniInstruktor = zeljeniInstruktor.toString()
log.debug(zeljeniInstruktor)

def instruktor = issueManager.getIssueByKeyIgnoreCase(zeljeniInstruktor)
log.debug(instruktor)

def ime = instruktor.getCustomFieldValue(imeField)
def prezime = instruktor.getCustomFieldValue(prezimeField)
def imeInstruktora = instruktor.getCustomFieldValue(imeInstruktoraField)

def string = ime.toString() + " " + prezime.toString()

log.debug(string)

imeInstruktoraField.updateValue(null, issue, new ModifiedValue(imeInstruktora, string), changeHolder)