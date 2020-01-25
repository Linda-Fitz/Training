import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import java.sql.Timestamp
import org.apache.log4j.Logger
import org.apache.log4j.Level
 
def log = Logger.getLogger("com.acme.TeorijaSve")
log.setLevel(Level.DEBUG)

issueManager = ComponentAccessor.getIssueManager()
issueFactory = ComponentAccessor.getIssueFactory()
projectManager = ComponentAccessor.getProjectManager()
subTaskManager = ComponentAccessor.getSubTaskManager()
changeHolder = new DefaultIssueChangeHolder()
cfManager = ComponentAccessor.getCustomFieldManager()
linkMgr = ComponentAccessor.getIssueLinkManager()
UserUtil userUtil = ComponentAccessor.getUserUtil()
visol = ComponentAccessor.getUserManager().getUserByName("VISOL")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()


//issue = issueManager.getIssueByKeyIgnoreCase("KAN-405")

sad = new Timestamp(System.currentTimeMillis())
predavacField = cfManager.getCustomFieldObject("customfield_10609")
predavacPPField = cfManager.getCustomFieldObject("customfield_10633")
datumVrijemeCasField = cfManager.getCustomFieldObject("customfield_10624")
datumVrijemeJutroField = cfManager.getCustomFieldObject("customfield_10611")
brojJutarnjihCasovaField = cfManager.getCustomFieldObject("customfield_11101")
datumVrijemePopodneField = cfManager.getCustomFieldObject("customfield_11102")
brojPopodnevnihCasovaField = cfManager.getCustomFieldObject("customfield_11103")
prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10618")
prisutniKandidatiLinkField = cfManager.getCustomFieldObject("customfield_10824")
sortKriterijum = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11104"))
prefSmjenaField = cfManager.getCustomFieldObject("customfield_10910")
datumZavrsetkaField = cfManager.getCustomFieldObject("customfield_10612")
polField = cfManager.getCustomFieldObject("customfield_10423")
datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")

imeField = cfManager.getCustomFieldObject("customfield_10132")
prezimeField = cfManager.getCustomFieldObject("customfield_10133")
telefonField = cfManager.getCustomFieldObject("customfield_10220")
predavac = issue.getCustomFieldValue(predavacField)
predavacPP = issue.getCustomFieldValue(predavacPPField)
datumVrijemeJutro = issue.getCustomFieldValue(datumVrijemeJutroField)
brojJutarnjihCasova = issue.getCustomFieldValue(brojJutarnjihCasovaField)
datumVrijemePopodne = issue.getCustomFieldValue(datumVrijemePopodneField)
brojPopodnevnihCasova = issue.getCustomFieldValue(brojPopodnevnihCasovaField)
vrijemePocetka = (datumVrijemeJutro) ? datumVrijemeJutro : datumVrijemePopodne
brojDana = issue.getCustomFieldValue(datumZavrsetkaField) - vrijemePocetka + 1
prisutniKandidati = issue.getCustomFieldValue(prisutniKandidatiField)
ime = predavac.getCustomFieldValue(imeField)
prezime = predavac.getCustomFieldValue(prezimeField)
telefon = predavac.getCustomFieldValue(telefonField)

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

GroovyShell shell = new GroovyShell()
def basePath = "C:/Program Files/Atlassian/Application Data/JIRA/scripts/helpers"
String query = "issuetype = Kandidat AND status in (\"AKTIVNO\",\"OTVORENO\")"
def queryIssue = shell.parse(new File("$basePath/general.groovy"))
def sviKandidati = queryIssue.executeJQL(query)

Boolean jutarnja = (datumVrijemeJutro && brojJutarnjihCasova > 0) ? true : false
Boolean popodnevna = (datumVrijemePopodne && brojPopodnevnihCasova > 0) ? true : false
Boolean obije = (jutarnja && popodnevna) ? true : false

prefJutarnja = []
prefPopodnevna = []
prefUndefined = []
polMusko = []
polZensko = []
polUndefined = []
godiste = [:]

for (i = 0; i <= sviKandidati.size() - 1; i++) {
    Issue kandidat = issueManager.getIssueByKeyIgnoreCase(sviKandidati[i].key)
    if(kandidat.getCustomFieldValue(prefSmjenaField)?.toString() == "Prijepodne") {
        prefJutarnja << kandidat
    } else if(kandidat.getCustomFieldValue(prefSmjenaField)?.toString() == "Popodne") {
        prefPopodnevna << kandidat
    } else {
        prefUndefined << kandidat
    }

    if(kandidat.getCustomFieldValue(polField)?.toString() == "Muško") {
        polMusko << kandidat
    } else if(kandidat.getCustomFieldValue(polField)?.toString() == "Žensko") {
        polZensko << kandidat
    } else {
        polUndefined << kandidat
    }   

    if (kandidat.getCustomFieldValue(datumRodjenjaField)) {
        def datumRodjenja = kandidat.getCustomFieldValue(datumRodjenjaField)
        godiste.put(kandidat, Math.floor((sad - datumRodjenja)/365).toInteger())
    } else {
        godiste.put(kandidat, 100)
    }
}
def popodnevneSmjene = []
def jutarnjeSmjene = []

if (obije) {
    prefUndefined.each { kandidat ->
        def brojKandidataPoGrupiPopodne = Math.ceil(prefPopodnevna.size() / brojPopodnevnihCasova)
        def brojKandidataPoGrupiUjutru = Math.ceil(prefJutarnja.size() / brojJutarnjihCasova)
        if (brojKandidataPoGrupiUjutru > brojKandidataPoGrupiPopodne) {
            prefJutarnja << kandidat
        } else {
            prefPopodnevna << kandidat
        }
    }
} 

brojDana.times {
    if (jutarnja && (vrijemePocetka + it) < datumVrijemeJutro + 1 && (vrijemePocetka + it) > datumVrijemeJutro - 1) {
        def vrijemePrveSmjene = datumVrijemeJutro
        prefUndefined.each {prefJutarnja << it}
        if (sortKriterijum?.toString() == "Pol") {
            jutarnjeSmjene = sortPoPolu(prefJutarnja, brojJutarnjihCasova)
        } else {
            jutarnjeSmjene = sortPoGodistu(prefJutarnja, brojJutarnjihCasova)
        }
        log.debug(kreirajTeorijaSubtaskove(jutarnjeSmjene, datumVrijemeJutro))
        datumVrijemeJutro += 1
    } 
    if (popodnevna && (vrijemePocetka + it) < datumVrijemePopodne + 1 && (vrijemePocetka + it) > datumVrijemePopodne - 1) {
        def vrijemePrveSmjene = datumVrijemePopodne
        prefUndefined.each {prefPopodnevna << it}
        if (sortKriterijum?.toString() == "Pol") {
            popodnevneSmjene = sortPoPolu(prefPopodnevna, brojPopodnevnihCasova)
        } else {
            popodnevneSmjene = sortPoGodistu(prefPopodnevna, brojPopodnevnihCasova)
        }
        log.debug(kreirajTeorijaSubtaskove(popodnevneSmjene, datumVrijemePopodne))
        datumVrijemePopodne += 1
    }   
}

log.debug(jutarnjeSmjene)
log.debug(popodnevneSmjene)

def sortPoPolu(kandidati, brojGrupa) {
    if (brojGrupa == 1) { return [kandidati]}

    def filteredM = []
    def filteredZ = []
    def filteredU = []
    kandidati.each { kandidat ->
        if (polMusko?.find{it == kandidat}) {
            filteredM << kandidat
        } else if (polZensko?.find{it == kandidat}){
            filteredZ << kandidat
        } else if (polUndefined?.find{it == kandidat}){
            filteredU << kandidat
        }
    }
    filteredU.each { unfilteredKandidat ->
        if (filteredM.size() > filteredZ.size()) {
            filteredZ << unfilteredKandidat
        } else {
            filteredM << unfilteredKandidat
        }
    }
    def kandidatiSorted = []
    def kandidataPoGrupi = Math.ceil((filteredM.size() + filteredZ.size()) / brojGrupa)
    brojGrupa.times { brojGrupe ->
        def grupaArray = []
        kandidataPoGrupi.times { kandidatIndex ->
            if (filteredM.size() > 0) {
                grupaArray << filteredM[0]
                filteredM.remove(0)
            } else if (filteredZ.size() > 0){
                grupaArray << filteredZ[0]
                filteredZ.remove(0)
            }
        }
        kandidatiSorted << grupaArray
    }
    return kandidatiSorted
}

def sortPoGodistu(kandidati, brojGrupa) {
    godiste = godiste.sort{ it.value }
    def filteredGodisteArray = []
    godiste.each { k, v ->
        if (kandidati.find{it == k}) {
            filteredGodisteArray << k
        }
    }

    def sveGrupe = []
    def brojKandidata = filteredGodisteArray.size()
    brojGrupa.times {
        def startrange = Math.ceil(it * brojKandidata / brojGrupa).toInteger()
        def endrange = Math.ceil((it + 1) * brojKandidata / brojGrupa).toInteger()
        def grupaArray = []
        for (i = startrange; i < endrange; i++) {
            grupaArray << filteredGodisteArray[i]
        }
        sveGrupe << grupaArray
    }
    return sveGrupe
}

def getPol(kandidat) {
    if(kandidat.getCustomFieldValue(polField)?.toString() == "Muško") {
        return "Muško"
    } else if(kandidat.getCustomFieldValue(polField)?.toString() == "Žensko") {
        return "Žensko"
    } else {
        return "Undefined"
    }   
}

def getGodiste(kandidat) {
    if (kandidat.getCustomFieldValue(datumRodjenjaField)) {
        def datumRodjenja = kandidat.getCustomFieldValue(datumRodjenjaField)
        return Math.floor((sad - datumRodjenja)/365).toInteger()
    } else {
        return 100
    }
}

def kreirajTeorijaSubtaskove(smjenaArray, pocetakPrveSmjene) {
    def pocetakSmjene = pocetakPrveSmjene
    def temparray = []
    smjenaArray.each {
        def pocetakStr = pocetakSmjene.toString()
        def year = pocetakStr.split("-")[0]
        def month = pocetakStr.split("-")[1]
        def day = pocetakStr.split("-")[2].split(" ")[0]
        def hour = pocetakStr.split(" ")[1].split(":")[0]
        def minute = pocetakStr.split(" ")[1].split(":")[1]
        def sum = "Čas teorija $day.$month.$year $hour:$minute" + "h" 
        temparray << sum

        MutableIssue casIssue = issueFactory.getIssue()
        casIssue.setSummary(sum)
        casIssue.setParentObject(issue)
        casIssue.setProjectObject(KAN)
        casIssue.setIssueTypeId("10401")
        casIssue.setAssignee(currentUser)
        casIssue.setReporter(currentUser)
        casIssue.setCustomFieldValue(prisutniKandidatiField, it)
        casIssue.setCustomFieldValue(datumVrijemeCasField, pocetakSmjene)       
        casIssue.setCustomFieldValue(predavacField, predavac)
        casIssue.setCustomFieldValue(predavacPPField, predavacPP)
        Map<String,Object> casIssueParams = ["issue" : casIssue] as Map<String,Object>
        issueManager.createIssueObject(visol, casIssueParams)
        subTaskManager.createSubTaskIssueLink(issue, casIssue, visol)

        prisutniKandidatiLinkField.updateValue(null, casIssue, new ModifiedValue(casIssue.getCustomFieldValue(prisutniKandidatiField), it),changeHolder)
        pocetakSmjene = new Timestamp(pocetakSmjene.getTime() +  (1000 * 60 * 60)) // + sat vremena
    }

    return temparray
}