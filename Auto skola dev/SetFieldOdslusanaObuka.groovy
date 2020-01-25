import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
def log = Logger.getLogger("com.acme.test")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()

//Issue issue = issueManager.getIssueByKeyIgnoreCase("CE-127")
//Issue parentIssue = issue.getParentObject()
def changeHolder = new DefaultIssueChangeHolder();
def cfManager = ComponentAccessor.getCustomFieldManager()

def imeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10132")
def prezimeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10133")
def ocevoField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10117")
def zavodniField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10321")
def vdField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10203")


def ime = issue.getParentObject().getCustomFieldValue(imeField);
def prezime = issue.getParentObject().getCustomFieldValue(prezimeField);
def ocevo= issue.getParentObject().getCustomFieldValue(ocevoField);
def zavodni = issue.getParentObject().getCustomFieldValue(zavodniField);
def vd = issue.getParentObject().getCustomFieldValue(vdField);


def imeField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10132")
def prezimeField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10133")
def ocevoField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10117")
def zavodniField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10321")
def vdField1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10203")

def ime1 = issue.getCustomFieldValue(imeField);
def prezime1 = issue.getCustomFieldValue(prezimeField);
def ocevo1= issue.getCustomFieldValue(ocevoField);
def zavodni1= issue.getCustomFieldValue(zavodniField);
def vd1= issue.getCustomFieldValue(vdField);


imeField1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue( imeField1), ime),changeHolder);
prezimeField1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue( prezimeField1), prezime),changeHolder);
ocevoField1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue( ocevoField1), ocevo),changeHolder);
zavodniField1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue( zavodniField1), zavodni),changeHolder);
vdField1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue( vdField1), vd),changeHolder);
def Obuka = "Odslušana obuka - $ime1 $prezime1 $ocevo1 $zavodni1 $vd1 - $ime $prezime $ocevo $zavodni $vd "

