import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.onresolve.jira.groovy.user.FieldBehaviours;
import com.onresolve.jira.groovy.user.FormField;

FormField parentObject = getFieldById("parentIssueId");
Long parentIssueId = parentObject.getFormValue() as Long
def issueManager = ComponentAccessor.getIssueManager()
def parentIssue = issueManager.getIssueObject(parentIssueId)

def nameSat = getFieldById("customfield_10407")//Name of Planet
def ageSat = getFieldById("customfield_10409")// Diameter of Planet
def distanceSatPlnt = getFieldById("customfield_10410")// Distance from Sun
def distanceSatSun = getFieldById("customfield_10411")// Length of year

def namePlanet = getFieldById("customfield_10402") //Name of Planet
def diameterPlanet = getFieldById("customfield_10403") // Diameter of Planet
def distancePlanet = getFieldById("customfield_10406") // Distance from Sun
def yearPlanet = getFieldById("customfield_10405") // Length of year

def prntnamePlanet = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10402")
def prntnamePlanetVal = parentIssue.getCustomFieldValue(prntnamePlanet)
namePlanet.setFormValue(prntnamePlanetVal)

def prntdiameterPlanet = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10403")
def prntdiameterPlanetVal = parentIssue.getCustomFieldValue(prntdiameterPlanet)
diameterPlanet.setFormValue(prntdiameterPlanetVal)

def prntdistancePlanet = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10406")
def prntdistancePlanetVal = parentIssue.getCustomFieldValue(prntdistancePlanet)
distancePlanet.setFormValue(prntdistancePlanetVal)

def prntyearPlanet = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10405")
def prntyearPlanetVal = parentIssue.getCustomFieldValue(prntyearPlanet)?.getOptionId()
yearPlanet.setFormValue(prntyearPlanetVal)
