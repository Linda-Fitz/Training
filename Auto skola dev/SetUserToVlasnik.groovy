import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.JQLManuel")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
def groupManager = ComponentAccessor.getGroupManager()
def userUtil = ComponentAccessor.getUserUtil()

//def issue = issueManager.getIssueByKeyIgnoreCase("CE-1307")

def vrstaZaposlenogField = cfManager.getCustomFieldObject("customfield_10308")
def vrstaZaposlenog = issue.getCustomFieldValue(vrstaZaposlenogField).toString()
log.debug(vrstaZaposlenog)

def imeField = cfManager.getCustomFieldObject("customfield_10820")
def ime = issue.getCustomFieldValue(imeField)
log.debug(ime)

def users = userUtil.getAllApplicationUsers()
def user

def postojeciUser = users.find { 
    if(it.getName() == ime){             
    user = it
	}
}

log.debug(user)

def group = groupManager.getGroup("Vlasnik")
log.debug(group.getName())

if(vrstaZaposlenog.contains("Vlasnik")){
    log.debug("Zaposleni: " + user.toString() + " pripada grupi: " + group.getName())
    
	groupManager.addUserToGroup(user, group)
}