import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZakazanPocetakListener")
log.setLevel(Level.DEBUG)

if (issue.getIssueTypeId() != "10601" && issue.getIssueTypeId() != "10802" && issue.getIssueTypeId() != "10703" ) { return } //dodatni cas, dopunski cas, redovni cas voznje

def zakazanPocetakChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Zakazan po")}
log.debug(zakazanPocetakChanged)
if (!zakazanPocetakChanged) {return }

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()

def newstr = zakazanPocetakChanged?.newstring
log.debug("new str: " + newstr)

def dan = newstr.split('/')[0]
def mesec = newstr.split('/')[1]
def godinaall = newstr.split('/')[2]
def godina = godinaall.split(' ')[0]
def datum = "${dan}-${mesec}-${godina} "
def vreme = newstr.split(' ')[1]
log.debug(datum)
log.debug(vreme)

def key = issue.key
def casIssue = issueManager.getIssueByKeyIgnoreCase(key)
log.debug("Cas issue: " + casIssue)

def sum = issue.getSummary()
sum = sum.toString()
log.debug("Old summary: " + sum)

def imeField = cfManager.getCustomFieldObject("customfield_10132")
def ime = issue.getCustomFieldValue(imeField)
def prezimeField = cfManager.getCustomFieldObject("customfield_10133")
def prezime = issue.getCustomFieldValue(prezimeField)

def imePrezime = ime + " " + prezime
log.debug(imePrezime)

def stringo

if (issue.getIssueTypeId() == "10601") {
    stringo = "Dodatni \u010Das vo\u017Enje | ${imePrezime} | "
} else if (issue.getIssueTypeId() == "10802") {
    stringo = "Dopunski \u010Das voznje | ${imePrezime} | "
} else {
    stringo = "Redovni \u010Das vo\u017Enje | ${imePrezime} | "
}

def zakazanPocetak = cfManager.getCustomFieldObject("customfield_10919")
def pocetak = casIssue.getCustomFieldValue(zakazanPocetak)
log.debug("pocetak: " + pocetak)
def zakazanKraj = cfManager.getCustomFieldObject("customfield_10920")
def kraj = casIssue.getCustomFieldValue(zakazanKraj)
log.debug(kraj)
kraj = kraj.toString()

def krajTime = kraj.split(' ')[1]
def hour = krajTime.split(':')[0]
def min = krajTime.split(':')[1]
log.debug(hour + ":" + min)

log.debug("New summary: ${stringo}" + datum + vreme + "h - " + hour + ":" + min + "h")

casIssue.setSummary("${stringo} " + datum + vreme + "h-" + hour + ":" + min + "h")
issueManager.updateIssue(currentUser, casIssue, EventDispatchOption.ISSUE_UPDATED, false)

