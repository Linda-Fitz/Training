import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.KuponTest")
log.setLevel(Level.DEBUG)

def cfManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()

def kuponField = getFieldById(getFieldChanged())
def kuponPolje = kuponField.getValue()
Issue kupon = issueManager.getIssueByKeyIgnoreCase(kuponPolje)

def odobrenPopustFF = getFieldById("customfield_10638")
def iznosFF =  getFieldById("customfield_10316")
def obukaFF = getFieldById("customfield_10507")
def popustKuponaField = getFieldById("customfield_11240")
def zaPlatitiRedovnaObukaFF = getFieldById("customfield_11256")
def iznosRateFF = getFieldById("customfield_10801")

def brojRataField = getFieldById("customfield_10639")
def brojRata = brojRataField.getValue().toDouble()
def iznosRate 

def odobrenPopust = cfManager.getCustomFieldObject("customfield_11240")
odobrenPopustFF.setFormValue(kupon.getCustomFieldValue(odobrenPopust))

def zaPlatitiIznos = zaPlatitiRedovnaObukaFF.getValue()
def popustIznos = odobrenPopustFF.getValue()

def popust = (zaPlatitiIznos.toDouble() * popustIznos.toDouble())/100 
def iznos = zaPlatitiIznos.toDouble() - popust.toDouble()
log.debug("Iznos:" +iznos)

iznosRate = iznos / brojRata
iznosFF.setFormValue(iznos)
iznosRateFF.setFormValue(iznosRate.toDouble())
zaPlatitiRedovnaObukaFF.setFormValue(iznos) 
