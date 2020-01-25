import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.ValidatorKupona")
log.setLevel(Level.DEBUG)

def attachmentManager = ComponentAccessor.getAttachmentManager()
def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder();
def cfManager = ComponentAccessor.getCustomFieldManager()

//def issue = issueManager.getIssueByKeyIgnoreCase("KAN-7654")

def attachments = attachmentManager.getAttachments(issue)

attachments.each {attachment ->
    def attachedFileName = attachment.getFilename();
    if(attachedFileName.contains("Poklon vau\u010Der")){
        log.debug(attachedFileName)
        invalidInputException = new InvalidInputException("Ve\u0107 postoji kupon izdat ovom kandidatu")
    }
}