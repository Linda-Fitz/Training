import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.OdobriDopunskeCasove")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
//issue = issueManager.getIssueByKeyIgnoreCase("KAN-878")

def brCasovaZaOdobrenjeField = cfManager.getCustomFieldObject("customfield_11276")
def brOdobrenihCasovaField = cfManager.getCustomFieldObject("customfield_10912")
def brOdobrenihMinutaField = cfManager.getCustomFieldObject("customfield_11405")
def brMinutaPoCasuField = cfManager.getCustomFieldObject("customfield_11404")
def brojPreostalihCasovaField = cfManager.getCustomFieldObject("customfield_11226")

// CONFIG
/* KUPI SA PRAKTICNE OBUKE BR MINUTA PO CASU
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def queryConfig =  jqlQueryParser.parseQuery("issuetype = Konfiguracija AND status = aktivno")
def resultsConfig = searchProvider.search(queryConfig, visol, PagerFilter.getUnlimitedFilter())
def configIssue = resultsConfig.getIssues()[0]
*/

// KALKULACIJA BROJA CASOVA
def brCasovaZaOdobrenje = (issue.getCustomFieldValue(brCasovaZaOdobrenjeField))? issue.getCustomFieldValue(brCasovaZaOdobrenjeField):0
log.debug(brCasovaZaOdobrenje)
def prakticnaObukaLink = linkMgr.getOutwardLinks(issue.id).find { it.getLinkTypeId() == 10504 && it.getDestinationObject().getIssueTypeId() == "10304" }
def prakticnaObukaIssue = prakticnaObukaLink.getDestinationObject()
log.debug(prakticnaObukaIssue.getCustomFieldValue(brOdobrenihCasovaField))
if (prakticnaObukaIssue.getCustomFieldValue(brOdobrenihCasovaField)) { brCasovaZaOdobrenje +=prakticnaObukaIssue.getCustomFieldValue(brOdobrenihCasovaField)}

def brOdobrenihMinuta = brCasovaZaOdobrenje * prakticnaObukaIssue.getCustomFieldValue(brMinutaPoCasuField)

def brPreostalih = (issue.getCustomFieldValue(brojPreostalihCasovaField))? issue.getCustomFieldValue(brojPreostalihCasovaField):0
log.debug(brPreostalih)
def brPreostalihCasova = (brPreostalih) ? brPreostalih + brCasovaZaOdobrenje : brCasovaZaOdobrenje


// UPDATE CF NA PRAKTICNOJ OBUCI KANDIDATA
brOdobrenihCasovaField.updateValue(null, prakticnaObukaIssue, new ModifiedValue(prakticnaObukaIssue.getCustomFieldValue(brOdobrenihCasovaField), brCasovaZaOdobrenje), changeHolder)
brOdobrenihCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brOdobrenihCasovaField), brCasovaZaOdobrenje), changeHolder)
brOdobrenihMinutaField.updateValue(null, prakticnaObukaIssue, new ModifiedValue(prakticnaObukaIssue.getCustomFieldValue(brOdobrenihMinutaField), brOdobrenihMinuta), changeHolder)
brojPreostalihCasovaField.updateValue(null, prakticnaObukaIssue, new ModifiedValue(prakticnaObukaIssue.getCustomFieldValue(brojPreostalihCasovaField), brPreostalihCasova), changeHolder)
brojPreostalihCasovaField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPreostalihCasovaField), brPreostalihCasova), changeHolder)
issue.setCustomFieldValue(brCasovaZaOdobrenjeField, null)

UserUtil userUtil = ComponentAccessor.getUserUtil()
WorkflowTransitionUtil workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")
log.debug(prakticnaObukaIssue)
/*
if (prakticnaObukaIssue.getStatusId() == "10301") {
 	workflowTransitionUtil.setIssue(prakticnaObukaIssue);
	workflowTransitionUtil.setAction (41)    // Nazad u status U postupku
    workflowTransitionUtil.validate();
    workflowTransitionUtil.progress();	
}
*/