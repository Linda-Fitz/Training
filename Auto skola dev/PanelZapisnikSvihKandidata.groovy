import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.bc.issue.search.SearchService
import java.text.DecimalFormatSymbols
import com.atlassian.jira.issue.Issue
import java.text.DecimalFormat
import com.atlassian.jira.bc.filter.DefaultSearchRequestService
import com.atlassian.jira.bc.JiraServiceContextImpl

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.WebPanel")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("KAN-1565")
def issue = context.issue as Issue
def issueKey = issue.getKey()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
def searchService = ComponentAccessor.getComponent(SearchService.class)
def searchRequestService = ComponentAccessor.getComponent(DefaultSearchRequestService.class)
visol = ComponentAccessor.getUserManager().getUserByKey("visol")

	query = "issuetype = \"Kandidat\" and status = \"10307\" and cf[11265] = \"$issueKey\""
    def SearchService.ParseResult parseResultI =  searchService.parseQuery(visol,query)
	if (!parseResultI.isValid()) { return }
	def queryOld = parseResultI.getQuery()
	searchResultI = searchService.search(visol, parseResultI.getQuery(), PagerFilter.getUnlimitedFilter())
    def issuesSize = searchResultI.getIssues().size()
	issuesS = searchResultI.getIssues()

def filterOld = "https://visoldev.ddns.net:8000/issues/?filter=11405"
def ctx = new JiraServiceContextImpl(visol)
searchRequestService.getOwnedFilters(visol).each{ filter->
log.debug(filter.name)
if(filter.name == 'ZAPISNIK SVIH KANDIDATA'){
log.debug(filter.query)
filter.setQuery(queryOld)
searchRequestService.updateFilter(ctx, filter)
}
}

writer.write("""<p style="font-size: 50px; font-family: 'Satisfy', cursive;"><span style="color: #0000ff;"><a href="https://visoldev.ddns.net:8000/issues/?filter=11405">${issuesSize}</a></span></p>""")
