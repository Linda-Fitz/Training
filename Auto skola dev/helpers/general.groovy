import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import java.util.Locale
import org.apache.log4j.Logger
import org.apache.log4j.Level

def getLogger() {
    def log = Logger.getLogger("Helpers.General")
    log.setLevel(Level.DEBUG)
    return log
}

def getUserEmails(Issue issue, boolean assignee, boolean reporter, boolean watchers) {
    def userEmails = []
    if (assignee) { userEmails << issue.getAssignee()?.getEmailAddress() }
    if (reporter) { userEmails << issue.getReporter()?.getEmailAddress() }
    if (watchers) {
        def watcherField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(10353)
        def watcherList = issue.getCustomFieldValue(watcherField)
        watcherList.each { userEmails << (it).getEmailAddress() }
    }
    getLogger().debug(userEmails)
    return userEmails.unique(false).findAll { it } // Elimisanje duplih i falsy
}

def executeJQL(String query) {
    def visol = ComponentAccessor.getUserUtil().getUserByName("VISOL")
    def searchService = ComponentAccessor.getComponent(SearchService.class)
	SearchService.ParseResult parseResult =  searchService.parseQuery(visol, query)
	if (!parseResult.isValid()) { return [] }
	def searchResult = searchService.search(visol, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
	return searchResult.getResults()
}