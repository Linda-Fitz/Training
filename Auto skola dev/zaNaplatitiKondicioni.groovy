import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import java.lang.Integer
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("ZaPlatiti")
log.setLevel(Level.DEBUG)
def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def changeHolder = new DefaultIssueChangeHolder()
def zapLatitiField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_11303")
def preostaloField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10800")
def iznosField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10806")
def ukupnoZapLatitiField =cfManager.getCustomFieldObject("customfield_11302")
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-3606")
Issue parentIssue = issue.getParentObject();

//def zaPlatitiParent = issue.getCustomFieldValue(zapLatitiField)
//log.debug(zaPlatitiParent)

def subtasks = parentIssue.getSubTaskObjects()
List<Double> nekiSubTasks = []
def suma1 = 0
subtasks.each{it->
    def suma = it.getCustomFieldValue(zapLatitiField).toDouble()
    suma1 = suma1 + suma  
	//log.debug(suma1)

}
log.debug(suma1)
zapLatitiField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(zapLatitiField), suma1.round(2)),changeHolder)
def zaPlatitiParent = parentIssue.getCustomFieldValue(zapLatitiField)
log.debug(zaPlatitiParent)
def iznos = parentIssue.getCustomFieldValue(iznosField)
  mad = suma1 - iznos
  def prekoraceniIznosField = cfManager.getCustomFieldObject("customfield_10819")
  
if (mad<0){
  mad = mad*(-1)
  prekoraceniIznosField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prekoraceniIznosField), mad.round(2)),changeHolder)
  preostaloField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(preostaloField), (double)0),changeHolder)
}else {
	prekoraceniIznosField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(prekoraceniIznosField), null),changeHolder)
	preostaloField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(preostaloField), mad.round(2)),changeHolder)
  }
  
ukupnoZapLatitiField.updateValue(null, parentIssue, new ModifiedValue(parentIssue.getCustomFieldValue(zapLatitiField), suma1.round(2)),changeHolder)
