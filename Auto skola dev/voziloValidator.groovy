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
def voziloField = cfManager.getCustomFieldObject("customfield_10324")
def vozilo = issue.getCustomFieldValue(voziloField)
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
if (zaposlen.toString().contains("Instruktor") && !vozilo) {
//if (!vozilo && instruktor){
  invalidInputException = new InvalidInputException("Instruktoru mora biti dodijeljeno vozilo!") 
}
