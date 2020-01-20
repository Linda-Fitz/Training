import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.onresolve.jira.groovy.user.FieldBehaviours;
import com.onresolve.jira.groovy.user.FormField;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
def log = Logger.getLogger("com.acme.rtrytyy")
log.setLevel(Level.DEBUG)

FormField parentObject = getFieldById("parentIssueId");
Long parentIssueId = parentObject.getFormValue() as Long
def issueManager = ComponentAccessor.getIssueManager()
def parentIssue = issueManager.getIssueObject(parentIssueId)

def nazivKupca = getFieldById("customfield_10107")//Naziv Kupca
def gradKupca = getFieldById("customfield_10110")//Grad Kupca
def drzavaKupca = getFieldById("customfield_10111")//Drzava Kupca

def nazivField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10107")
def nazivValue = parentIssue.getCustomFieldValue(nazivField)
nazivKupca.setFormValue(nazivValue)

def gradField =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10110")
def gradValue = parentIssue.getCustomFieldValue(gradField)
gradKupca.setFormValue(gradValue)

def drzavaField =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10111")
def drzavaValue = parentIssue.getCustomFieldValue(drzavaField)
drzavaKupca.setFormValue(drzavaValue)



def fieldAId = getFieldById("customfield_10200")
def fieldBId = getFieldById("customfield_10201")
def fieldCId = getFieldById("customfield_10202")
def date = getFieldById("customfield_10204")
def selector = getFieldById("customfield_10205")
def checkBox1 = getFieldById("customfield_10203")

def parentFieldA = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10200")
def parentValueA = parentIssue.getCustomFieldValue(parentFieldA)
fieldAId.setFormValue(parentValueA)

def parentFieldB = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10201")
def parentValueB = parentIssue.getCustomFieldValue(parentFieldB)
fieldBId.setFormValue(parentValueB)

def parentFieldC = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10202")
def parentValueC = parentIssue.getCustomFieldValue(parentFieldC)
fieldCId.setFormValue(parentValueC)

def parentSelector = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10205")
def parentValueSelector = parentIssue.getCustomFieldValue(parentSelector)?.getOptionId()
selector.setFormValue(parentValueSelector)

def parentDate = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10204")
def parentValueDate = parentIssue.getCustomFieldValue(parentDate)
date.setFormValue(parentValueDate)
