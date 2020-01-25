import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import com.opensymphony.workflow.InvalidInputException

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-756")
def brojInstruktorskeField = cfManager.getCustomFieldObject("customfield_10214")
def brojInstruktorske = issue.getCustomFieldValue(brojInstruktorskeField)
def istekInstruktorskeField = cfManager.getCustomFieldObject("customfield_10418")
def istekInstruktorske = issue.getCustomFieldValue(istekInstruktorskeField)
def brojLjekarskogField = cfManager.getCustomFieldObject("customfield_10215")
def brojLjekarskog = issue.getCustomFieldValue(brojLjekarskogField)
def datumLjekarskogField = cfManager.getCustomFieldObject("customfield_10212")
def datumLjekarskog = issue.getCustomFieldValue(datumLjekarskogField)
def datumIstekaLjekarskogField = cfManager.getCustomFieldObject("customfield_10417")
def datumIstekaLjekarskog = issue.getCustomFieldValue(datumIstekaLjekarskogField)
def zaposlenField = cfManager.getCustomFieldObject("customfield_10308")
def zaposlen = issue.getCustomFieldValue(zaposlenField).toString()
Boolean instruktor

if (zaposlen instanceof Collection) {
 zaposlen.each { it ->
     if (it == "Instruktor") {
         instruktor = true
     }
 }
    
} else if (zaposlen instanceof String) {
    if (zaposlen == "Instruktor") {
        instruktor = true
    }
}
if (zaposlen.toString().contains("Instruktor") && !datumIstekaLjekarskog ) {
//if (!vozilo && instruktor){
  invalidInputException = new InvalidInputException("Popunite sva polja u tabu 'Dozvole'!") 
}
