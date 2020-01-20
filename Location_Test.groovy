import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import com.atlassian.jira.issue.Issue

def nazivLocation = getFieldById("customfield_10114")
def adresaLocation = getFieldById("customfield_10115")
def gradLocation = getFieldById("customfield_10116")
def pbLocation = getFieldById("customfield_10119")
def drzavaLocation = getFieldById("customfield_10118")
def deliveryLocation = getFieldById("customfield_10304")


def nazivLct = nazivLocation.getValue()
def adresaLct = adresaLocation.getValue()
def gradLct = gradLocation.getValue()
def pbLct = pbLocation.getValue()
def drzavaLct = drzavaLocation.getValue()
def deliveryLct = deliveryLocation.getValue()

adresaLocation.setFormValue(deliveryLct)

def issueManager = ComponentAccessor.getIssueManager()
Issue deliveryIssue = issueManager.getIssueByKeyIgnoreCase(deliveryLct.toString())

def nazivField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10107")
def nazivValue = deliveryIssue.getCustomFieldValue(nazivField)
nazivLocation.setFormValue(nazivValue)

def adresaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10108")
def adresaValue = deliveryIssue.getCustomFieldValue(adresaField)
adresaLocation.setFormValue(adresaValue)


def gradField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10110")
def gradValue = deliveryIssue.getCustomFieldValue(gradField)
gradLocation.setFormValue(gradValue)


def pbField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10109")
def pbValue = deliveryIssue.getCustomFieldValue(pbField)
pbLocation.setFormValue(pbValue)

def drzavaField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10111")
def drzavaValue = deliveryIssue.getCustomFieldValue(drzavaField)
drzavaLocation.setFormValue(drzavaValue)
