import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import groovy.json.*
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.ZavrsenCasVoznje")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
def changeHolder = new DefaultIssueChangeHolder()
def cfManager = ComponentAccessor.getCustomFieldManager()
def visol = ComponentAccessor.getUserManager().getUserByKey("visol")
def linkMgr = ComponentAccessor.getIssueLinkManager()
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class )
workflowTransitionUtil.setUserkey("visol")

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-282")
def parent = issue.getParentObject()

def brojOdobrenihCasovaField = cfManager.getCustomFieldObject("customfield_10912")
def brojOdvezenihCasovaField = cfManager.getCustomFieldObject("customfield_11606")
def brojPreostalihDopCasovaField = cfManager.getCustomFieldObject("customfield_11226")
def brojPreostalihRedCasovaField = cfManager.getCustomFieldObject("customfield_11700")

def vrstaPlacanjaField = cfManager.getCustomFieldObject("customfield_10320")
def ugovorenBrMinObukeField = cfManager.getCustomFieldObject("customfield_11400")
def odvezenBrMinRedField = cfManager.getCustomFieldObject("customfield_11306")
def preostaliMinRedField = cfManager.getCustomFieldObject("customfield_11403")
def brMinKonfField = cfManager.getCustomFieldObject("customfield_11404")
def zakPocetakCasaField = cfManager.getCustomFieldObject("customfield_10919")
def zakKrajCasaField = cfManager.getCustomFieldObject("customfield_10920")
def zakTrajanjeField = cfManager.getCustomFieldObject("customfield_10921")
def stvPocetakCasaField = cfManager.getCustomFieldObject("customfield_10915")
def stvKrajCasaField = cfManager.getCustomFieldObject("customfield_10916")
def stvTrajanjeField = cfManager.getCustomFieldObject("customfield_10917")
def brojCasovaVoznjeField = cfManager.getCustomFieldObject("customfield_11226")
def ukupnozakTrajanjeField = cfManager.getCustomFieldObject("customfield_11227")
def brojDopunskihCasovaVoznjeField = cfManager.getCustomFieldObject("customfield_11231")
def zaPlatitiDopField = cfManager.getCustomFieldObject("customfield_11303")
def odvezenBrMinDopField = cfManager.getCustomFieldObject("customfield_11406")
def zaPlatitiRedovnaField = cfManager.getCustomFieldObject("customfield_11256")
def prekoracenoField = cfManager.getCustomFieldObject("customfield_10819")
def ukupnoField = cfManager.getCustomFieldObject("customfield_11302")
def preostaloField = cfManager.getCustomFieldObject("customfield_10800")
def odobreniCasoviField = cfManager.getCustomFieldObject("customfield_11405")

def zakPocetakCasa = issue.getCustomFieldValue(zakPocetakCasaField)
def zakKrajCasa = issue.getCustomFieldValue(zakKrajCasaField)
def zakTrajanjeCasa = issue.getCustomFieldValue(zakTrajanjeField)

def stvPocetakCasa = issue.getCustomFieldValue(stvPocetakCasaField)
def stvKrajCasa = issue.getCustomFieldValue(stvKrajCasaField)
def stvPocetakCasaMins = stvPocetakCasa.getTime() / 1000 / 60
def stvKrajCasaMins = stvKrajCasa.getTime() / 1000 / 60
def stvTrajanje = stvKrajCasaMins - stvPocetakCasaMins
stvTrajanjeField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(stvTrajanjeField), (double)stvTrajanje), changeHolder)

def brMinutaPoCasu = parent.getCustomFieldValue(brMinKonfField)
def parentKandidatLink = linkMgr.getInwardLinks(parent.id).find { it.getLinkTypeId() == 10504 && it.getSourceObject().getIssueTypeId() == "10200" }
def kandidatIssue = parentKandidatLink.getSourceObject()
def vozilo = kandidatIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324")) // Vozilo issue field
def vrstaPlacanja = kandidatIssue.getCustomFieldValue(vrstaPlacanjaField).toString()

// UPDATE FIELDOVA
if (issue.getIssueTypeId() == "10802") { //DOPUNSKI
  // TRANSITION KANDIDAT
            if (kandidatIssue.getStatusId() == "10202") { 
                workflowTransitionUtil.setAction(131); // ZAVRSI REDOVNU OBUKU
                workflowTransitionUtil.setIssue(kandidatIssue);    
                workflowTransitionUtil.validate();
                workflowTransitionUtil.progress();
            }
  def odobreni = parent.getCustomFieldValue(odobreniCasoviField)
  odobreniCasoviField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(odobreniCasoviField), odobreni), changeHolder)

  def odvezeniDopunski = parent.getSubTaskObjects().findAll {it.getIssueTypeId() == "10802" && it.getStatusId() == "10313" }
  def brojMinuta = 0
  odvezeniDopunski.each { brojMinuta += it.getCustomFieldValue(zakTrajanjeField) }
  
  def brojOdobrenihCasova =  parent.getCustomFieldValue(brojOdobrenihCasovaField)
  def brojOdvezenihCasova = (brojMinuta / brMinutaPoCasu).round() 
  def brojPreostalihDopCasova = brojOdobrenihCasova - brojOdvezenihCasova

  odvezenBrMinDopField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(odvezenBrMinDopField), (double)brojMinuta), changeHolder)
  odvezenBrMinDopField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(odvezenBrMinDopField), (double)brojMinuta), changeHolder)
  brojOdvezenihCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojOdvezenihCasovaField), (double)brojOdvezenihCasova), changeHolder)
  brojPreostalihDopCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojPreostalihDopCasovaField), (double)brojPreostalihDopCasova), changeHolder)
  brojOdvezenihCasovaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojOdvezenihCasovaField), (double)brojOdvezenihCasova), changeHolder)
  brojPreostalihDopCasovaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojPreostalihDopCasovaField), (double)brojPreostalihDopCasova), changeHolder)

if (brojMinuta >= odobreni && kandidatIssue.getStatusId() == "10309") {
    WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
    workflowTransitionUtil.setIssue(kandidatIssue)
    workflowTransitionUtil.setAction(561)
    workflowTransitionUtil.validate()
    workflowTransitionUtil.progress()
  }
  // CIJENAdef 
  def cijenaIssue
  if (vozilo) {
    for(IssueLink linkCijena in ComponentAccessor.getIssueLinkManager().getInwardLinks(vozilo.id)) {
        def issueLinkTypeName = linkCijena.issueLinkType.name
        def linkedIssue = linkCijena.getSourceObject()
        def linkedIssueType = linkedIssue.getIssueTypeId()
        if (linkedIssueType == "10300" && linkedIssue.getStatusId() == "10100" && linkedIssue.getCustomFieldValue(vrstaPlacanjaField).toString() == vrstaPlacanja) {
          cijenaIssue = linkedIssue         
        } 
      }
  } 

  if (!cijenaIssue) {
    def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
    def searchProvider = ComponentAccessor.getComponent(SearchProvider)
    GroovyShell shell = new GroovyShell()
    def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
    String query = "issuetype = Cjenovnik AND status = aktivno AND cf[10320] = \"$vrstaPlacanja\" AND cf[10810] IS EMPTY"
    def queryIssue = shell.parse(new File("$basePath/general.groovy"))
    def cijene = queryIssue.executeJQL(query)

    cijenaIssue = cijene[0]
  }

    // ZAPLATITI
  if (!issue.getCustomFieldValue(zaPlatitiDopField)) {
     cijenaDopunskog = cijenaIssue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10924"))
     zaPlatitiCas = cijenaDopunskog.toDouble() * stvTrajanje / brMinutaPoCasu.toDouble()
    zaPlatitiDopField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(zaPlatitiDopField), (double)zaPlatitiCas), changeHolder)   
  }

  // CIFRE NA KADNIDATU
  /*def zaPlatitiDop = 0
  odvezeniDopunski.each { dopunski ->
    def zaPl = dopunski.getCustomFieldValue(zaPlatitiDopField)
    if (zaPl) {
      if(zaPl > 0) {
        zaPlatitiDop += zaPl
      }
    }
  } */
    def zaPl = kandidatIssue.getCustomFieldValue(zaPlatitiDopField).toDouble()
    def zaPlatitiDop = (issue.getCustomFieldValue(zaPlatitiDopField))? issue.getCustomFieldValue(zaPlatitiDopField).toDouble() :0
    zaPl = zaPl + zaPlatitiCas
//zaPl = zaPl + zaPlatitiDop
log.debug(zaPlatitiDop)
  
  def zaPlatitiRedovna = kandidatIssue.getCustomFieldValue(zaPlatitiRedovnaField).toDouble()
  log.debug(zaPlatitiRedovna)
  def prekoraceno = kandidatIssue.getCustomFieldValue(prekoracenoField)
  def ukupno = kandidatIssue.getCustomFieldValue(ukupnoField).toDouble()
  def preostalo = kandidatIssue.getCustomFieldValue(preostaloField)

  if (prekoraceno > 0) {
    if (prekoraceno >= zaPlatitiDop) {
      prekoraceno = prekoraceno - zaPlatitiDop
      zaPlatitiDop = (double)0
    } else {
      prekoraceno = (double)0
      zaPlatitiDop = zaPlatitiDop - prekoraceno
    }
  } 
  ukupno = ukupno + zaPlatitiCas
  preosta = preostalo + zaPlatitiCas
log.debug(ukupno)   
  prekoracenoField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(prekoracenoField), prekoraceno), changeHolder)
  zaPlatitiDopField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(zaPlatitiDopField), (double)zaPl), changeHolder)
  ukupnoField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(zaPlatitiDopField), (double)ukupno), changeHolder)
  preostaloField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(preostaloField), preosta), changeHolder)


} else if (issue.getIssueTypeId() == "10703") { // REDOVNI
    
  def odvezeniRedovni = parent.getSubTaskObjects().findAll { it.getIssueTypeId() == "10703" && (it.getStatusId() == "10313" || it.getStatusId() == "10502") }
  def brojMinuta = 0
  odvezeniRedovni.each { brojMinuta += (it.getCustomFieldValue(zakTrajanjeField)) ? it.getCustomFieldValue(zakTrajanjeField) : 0 }
  def preostaloMinuta = parent.getCustomFieldValue(ugovorenBrMinObukeField) - brojMinuta
  if (preostaloMinuta < 0) { preostaloMinuta = 0 }
  def preostaloSati = (preostaloMinuta / brMinutaPoCasu).round()
    
    def brojOdvezenihCasova = (brojMinuta / brMinutaPoCasu).round() 
  def brojPreoRedCasova = 30 - brojOdvezenihCasova

  odvezenBrMinRedField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(odvezenBrMinRedField), (double)brojMinuta), changeHolder)
  preostaliMinRedField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(preostaliMinRedField), (double)preostaloMinuta), changeHolder)

  odvezenBrMinRedField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(odvezenBrMinRedField), (double)brojMinuta), changeHolder)
  preostaliMinRedField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(preostaliMinRedField), (double)preostaloMinuta), changeHolder)

  brojPreostalihRedCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojPreostalihRedCasovaField), (double)preostaloSati), changeHolder)
  brojPreostalihRedCasovaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojPreostalihRedCasovaField), (double)preostaloSati), changeHolder)

  brojPreostalihDopCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(kandidatIssue.getCustomFieldValue(brojPreostalihDopCasovaField), (double)brojPreoRedCasova), changeHolder)
  //brojPreostalihDopCasovaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojPreostalihDopCasovaField), (double)brojPreoRedCasova), changeHolder)
    brojOdvezenihCasovaField.updateValue(null, kandidatIssue, new ModifiedValue(parent.getCustomFieldValue(brojOdvezenihCasovaField), (double)brojOdvezenihCasova), changeHolder)
    brojOdvezenihCasovaField.updateValue(null, parent, new ModifiedValue(parent.getCustomFieldValue(brojOdvezenihCasovaField), (double)brojOdvezenihCasova), changeHolder)
    
  if (preostaloSati == 0 && kandidatIssue.getStatusId() == "10202") { //voznja obuka
    WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class); 
    workflowTransitionUtil.setUserkey("visol")
    workflowTransitionUtil.setIssue(kandidatIssue)
    workflowTransitionUtil.setAction(131)
    workflowTransitionUtil.validate()
    workflowTransitionUtil.progress()
  }
} 

// GENERISANJE TRACKING LINKA
def baseUrl = "https://asteam.ddns.net:8085/api/reports/route?deviceId="
def deviceId = vozilo?.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11225"))
if (deviceId) {
  def authString = "YWRtaW46a2Vha29l" 
  def from = getFormattedStr(stvPocetakCasa.toString())
  def to = getFormattedStr(stvKrajCasa.toString())
  def url = baseUrl + deviceId + "&groupId=6&from=$from&to=$to"
  def connection = url.toURL().openConnection()
  Boolean hasTracking = true
  try {
    connection.addRequestProperty("Authorization", "Basic $authString")
    connection.addRequestProperty("Content-Type", "application/json")
    connection.addRequestProperty("Accept", "application/json")
    connection.setRequestMethod("GET")
    connection.connect()
    def response = connection.content.text
    def json = new JsonSlurper().parseText(response)

    if (json.size() > 0) {
      def start = json[0].attributes.totalDistance.toDouble()
      def end = json[json.size() - 1].attributes.totalDistance.toDouble()
      def distance = ((start - end) / 100000).round(2)
      def predjanoKilometaraField = cfManager.getCustomFieldObject("customfield_10513")
      predjanoKilometaraField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(predjanoKilometaraField), distance), changeHolder)     
    } else {
      hasTracking = false
    }
  } catch (ex) {
    log.debug(ex.getMessage())
  }

  // TRACCAR URL
  if (hasTracking) {
    def trackUrl = "https://asteam.ddns.net:3000/Traccar.html?"
    trackUrl += "deviceId=$deviceId&as=$authString&from=$from&to=$to"
    def trackingUrlField = cfManager.getCustomFieldObject("customfield_11224")
    trackingUrlField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(trackingUrlField), trackUrl), changeHolder)   
  }
}


def getFormattedStr(dateTime) {
  def hour = dateTime.split(" ")[1].split(":")[0]
  def minute = dateTime.split(" ")[1].split(":")[1]

  return dateTime.split(" ")[0] + "T$hour" + ":$minute" + ":00Z"  
}