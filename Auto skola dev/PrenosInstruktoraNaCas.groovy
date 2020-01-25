import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.user.util.UserUtil
import org.apache.log4j.Logger
import org.apache.log4j.Level

log = Logger.getLogger("com.acme.PrenosInstruktoraNaCas")
log.setLevel(Level.DEBUG)

def visol = ComponentAccessor.getUserUtil().getUserByName("visol")
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def changeHolder = new DefaultIssueChangeHolder()

def zeljeniField = cfManager.getCustomFieldObject("customfield_10318") 
def dodijeljeniField = cfManager.getCustomFieldObject("customfield_10913") 
def kandidatKeyField = cfManager.getCustomFieldObject("customfield_11601")
//def issue = issueManager.getIssueObject("KAN-4147")

// POST FUNCTION CREATE
def kandidatIssue
if(issue.getIssueTypeId() == "10601") { // Dodatni(kondicioni)
	kandidatIssue = issue.getParentObject()
} else {
	def prakticnaObukaIssue = issue.getParentObject()
	def linkToKandidat = linkMgr.getInwardLinks(prakticnaObukaIssue.id).find { it.getLinkTypeId() == 10504 }
	if (!linkToKandidat) { return }
	kandidatIssue = linkToKandidat.getSourceObject()
}
def instruktor = kandidatIssue.getCustomFieldValue(dodijeljeniField) ?: kandidatIssue.getCustomFieldValue(dodijeljeniField)
dodijeljeniField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(dodijeljeniField), instruktor), changeHolder)
kandidatKeyField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(kandidatKeyField), kandidatIssue.key), changeHolder)

// RETROAKTIVNO AZURIRANJE
/*
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
query = "issuetype in (\"Dodatni \u010Das\",\"Redovni \u010Das\",\"Dopunski \u010Das\")"
results = searchProvider.search(query, visol, PagerFilter.getUnlimitedFilter())
issues = results.getIssues()

issues.each { it ->
	def issue = issueManager.getIssueObject(it.key)
	def kandidatIssue
	if(issue.getIssueTypeId() == "10601") { // Dodatni(kondicioni)
		kandidatIssue = issue.getParentObject()
	} else {
		def prakticnaObukaIssue = issue.getParentObject()
		def linkToKandidat = linkMgr.getInwardLinks(prakticnaObukaIssue.id).find { it.getLinkTypeId() == 10504 }
		if (!linkToKandidat) { return }
		kandidatIssue = linkToKandidat.getSourceObject()
	}
	def instruktor = kandidatIssue.getCustomFieldValue(dodijeljeniField) ?: kandidatIssue.getCustomFieldValue(dodijeljeniField)
	dodijeljeniField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(dodijeljeniField), instruktor), changeHolder)
	kandidatKeyField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(kandidatKeyField), kandidatIssue.key), changeHolder)
}
*/