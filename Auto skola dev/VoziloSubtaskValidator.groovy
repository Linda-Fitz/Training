import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.VoziloSubtaskValidator")
log.setLevel(Level.DEBUG)

def parent = issue.getParentObject()

if (parent.getStatusId() == "10102" || parent.getStatusId() == "10104") {
	invalidInputException = new InvalidInputException("Nije mogu\u0107e kreirati stavku na otvorenom i rashodovanom vozilu.")	
}
