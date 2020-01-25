import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.CasTeorijeDatumZavrsetka")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def changeHolder = new DefaultIssueChangeHolder()
def datumZavrsetkaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10630")

def datumField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10624")
//Issue issue = issueManager.getIssueByKeyIgnoreCase("KAN-265")
Issue parentIssue = issue.getParentObject();
def subtasks = parentIssue.getSubTaskObjects()
log.debug(parentIssue)
log.debug(subtasks)

subtasks.sort{a,b ->
	a.getCustomFieldValue(datumField) <=> b.getCustomFieldValue(datumField)
}

def datumStr = subtasks.last().getCustomFieldValue(datumField).toString().split(" ")[0]
def date = new Date().parse("yyyy-MM-dd", datumStr)

for (IssueLink link in linkMgr.getInwardLinks(parentIssue.id)) {
	if (link.getLinkTypeId == 10500) {
		def teorijaIssue = link.getSourceObject()
		datumZavrsetkaField.updateValue(null, teorijaIssue, new ModifiedValue(teorijaIssue.getCustomFieldValue(datumZavrsetkaField), date.toTimestamp()),changeHolder)    		
	}
}

