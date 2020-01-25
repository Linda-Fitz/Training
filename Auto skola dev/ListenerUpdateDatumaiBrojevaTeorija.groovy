if (issue.getIssueTypeId() != "10200" ) { return } //kandidat

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import java.text.SimpleDateFormat

import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.PromenaVrednostiPoljaListener")
log.setLevel(Level.DEBUG)

def brojLicneIspraveChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Broj li")}
log.debug("Broj licne changed: "+brojLicneIspraveChanged)

def brojLekarskogUverenjaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Broj ljekarskog uvjerenja")}
log.debug("Broj lekarskog changed: "+brojLekarskogUverenjaChanged)

def datumLekarskogUverenjaChanged = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field.toString().contains("Datum ljekarskog uvjerenja")}
log.debug("Datum lekarskog changed: "+datumLekarskogUverenjaChanged)

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def issueManager = ComponentAccessor.getIssueManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")

def brojLicneIspraveString = brojLicneIspraveChanged?.newstring
log.debug("broj licne isprave: " + brojLicneIspraveString)

def brojLekarskogUverenjaString = brojLekarskogUverenjaChanged?.newstring
log.debug("broj lekarskog uverenja: " + brojLekarskogUverenjaString)

def datumLekarskogUverenjaString = datumLekarskogUverenjaChanged?.newstring
log.debug("datum lekarskog uverenja: " + datumLekarskogUverenjaString)

def prijavljenIspit = issue.getSubTaskObjects().findAll { it.getIssueTypeId() == "10405" }
log.debug(prijavljenIspit)

prijavljenIspit.each{
	
	log.debug("Prijavljen ispit: " + it)
	MutableIssue prijavljenIspitIssue = (MutableIssue) ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(it.toString())
		
	def datumLekarskogField = cfManager.getCustomFieldObject("customfield_10212")
	def datumLek = prijavljenIspitIssue.getCustomFieldValue(datumLekarskogField)
	log.debug("datum lek: " + datumLek)
	
	def brojLicneFild = cfManager.getCustomFieldObject("customfield_10216")
	def brojLic = prijavljenIspitIssue.getCustomFieldValue(brojLicneFild)
	log.debug("broj licne: " + brojLic)
	
	def brojLekarskogField = cfManager.getCustomFieldObject("customfield_10215")
	def brojLekar = prijavljenIspitIssue.getCustomFieldValue(brojLekarskogField)
	log.debug("broj lekar: " + brojLekar)

	Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(datumLekarskogUverenjaString); 
    
    if (datumLekarskogUverenjaString != null || brojLicneIspraveString != null || brojLekarskogUverenjaString != null) {
	
        datumLekarskogField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(datumLek, date1.toTimestamp()), changeHolder)
		brojLicneFild.updateValue(null, prijavljenIspitIssue, new ModifiedValue(brojLic, brojLicneIspraveString), changeHolder)
    	brojLekarskogField.updateValue(null, prijavljenIspitIssue, new ModifiedValue(brojLekar, brojLekarskogUverenjaString), changeHolder)
    }
	issueManager.updateIssue(visol, prijavljenIspitIssue, EventDispatchOption.ISSUE_UPDATED, false)
}
issueManager.updateIssue(visol, issue, EventDispatchOption.ISSUE_UPDATED, false)