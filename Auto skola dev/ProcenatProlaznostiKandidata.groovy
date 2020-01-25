import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.ProcenatProlaznosti")
log.setLevel(Level.DEBUG)

def visol = ComponentAccessor.getUserUtil().getUserByName("visol")
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def changeHolder = new DefaultIssueChangeHolder()

//def issue = issueManager.getIssueObject("KAN-7917")
def procenatProlaznostiField = cfManager.getCustomFieldObject("customfield_11602")
def dodijeljeniInstruktorField = cfManager.getCustomFieldObject("customfield_10913")

def instruktorIssue = issue.getCustomFieldValue(dodijeljeniInstruktorField)
def instruktorKey = instruktorIssue.key

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = kandidat AND cf[10913] = $instruktorKey"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def issues = queryIssue.executeJQL(query)
log.debug(issues.size())

double polozili = 0
double nisuPolozili = 0

issues.each {
    def kandidat = issueManager.getIssueObject(it.key)

    if (kandidat.getStatusId() in ["10204","10205"]) { 
        polozili += 1
    } else if (kandidat.getStatusId() == "10308" ){
        nisuPolozili += 1
        log.debug(nisuPolozili)
        /*
        def linkToIspit = linkMgr.getOutwardLinks(kandidat.id)?.find { 
            it.getDestinationObject().getIssueTypeId() == "10704" &&
            it.getDestinationObject().getStatusId() == "10313" }
        if (linkToIspit) { nisuPolozili += 1 }
    }*/

}
}
double procenat 
if (polozili == 0) {
    procenat = 0 
} else if (nisuPolozili == 0) {
    procenat = 100
} else {
    procenat = (polozili /(polozili + nisuPolozili) * 100).round(2)
    log.debug(procenat)
}

procenatProlaznostiField.updateValue(null, instruktorIssue, new ModifiedValue(instruktorIssue.getCustomFieldValue(procenatProlaznostiField), procenat), changeHolder)