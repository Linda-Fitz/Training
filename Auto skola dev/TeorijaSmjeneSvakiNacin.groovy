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
visol = ComponentAccessor.getUserManager().getUserByKey("visol")
KAN = projectManager.getProjectObjects().find {it.getKey() == "KAN"}
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-2875")

sad = new Timestamp(System.currentTimeMillis())
predavacField = cfManager.getCustomFieldObject("customfield_10609")
predavacPPField = cfManager.getCustomFieldObject("customfield_10633")
datumVrijemeCasField = cfManager.getCustomFieldObject("customfield_10624")
datumVrijemeJutroField = cfManager.getCustomFieldObject("customfield_10611")
brojJutarnjihCasovaField = cfManager.getCustomFieldObject("customfield_11101")
datumVrijemePopodneField = cfManager.getCustomFieldObject("customfield_11102")
brojPopodnevnihCasovaField = cfManager.getCustomFieldObject("customfield_11103")
prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10618")
kandidatiLinkField = cfManager.getCustomFieldObject("customfield_11222")
sortKriterijum = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11104"))
prefgrupaField = cfManager.getCustomFieldObject("customfield_10910")
datumZavrsetkaField = cfManager.getCustomFieldObject("customfield_10612")
polField = cfManager.getCustomFieldObject("customfield_10423")
datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")
def defaultpredavacField = cfManager.getCustomFieldObject("customfield_11304")
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
/*
def querystring1 = "issuetype = Konfiguracija AND status in (\"AKTIVNO\")"
def query1 =  jqlQueryParser.parseQuery(querystring1)
def mainres1 = searchProvider.search(query, visol, PagerFilter.getUnlimitedFilter())
def konfig = mainres1.getIssues()[0]
def pred = konfig.getCustomFieldValue(defaultpredavacField)
*/

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
    if(kandidat.getCustomFieldValue(prefgrupaField)?.toString() == "Prijepodne") {
        prefJutarnja << kandidat
    } else if(kandidat.getCustomFieldValue(prefgrupaField)?.toString() == "Popodne") {
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

if (obije) { // Rasporedjuje kandidate tako da tezi da ima jednak broj po smjeni u jutarnjim, odn. popodnevnim casovima
    prefUndefined.each { kandidat -> 
        def brojKandidataPoGrupiPopodne = Math.ceil(prefPopodnevna.size() / brojPopodnevnihCasova)
        def brojKandidataPoGrupiUjutru = Math.ceil(prefJutarnja.size() / brojJutarnjihCasova)
        if (brojKandidataPoGrupiUjutru < brojKandidataPoGrupiPopodne) {
            prefJutarnja << kandidat
        } else {
            prefPopodnevna << kandidat
        }
    }
} else if (jutarnja) { // Ako su samo jutarnje organizovane, prebacuje sve u jutarnje
    prefUndefined.each { kandidat ->
        prefJutarnja << kandidat
    }
    prefPopodnevna.each { kandidat ->
        prefJutarnja << kandidat
    }
} else if (popodnevna) { // Ako su samo popodnevne
    prefUndefined.each { kandidat ->
        prefPopodnevna << kandidat
    }
    prefJutarnja.each { kandidat ->
        prefPopodnevna << kandidat
    }
}

// Kreiranje grupa
if (jutarnja) { 
    log.debug(prefJutarnja)
    if (sortKriterijum?.toString() == "Pol") {
        jutarnjeSmjene = sortPoPolu(prefJutarnja, brojJutarnjihCasova)
    } else {
        jutarnjeSmjene = sortPoGodistu(prefJutarnja, brojJutarnjihCasova)
    }
    log.debug(jutarnjeSmjene)
    log.debug(kreirajSmjeneISubtaskove(jutarnjeSmjene, datumVrijemeJutro, "J-"))
}

if (popodnevna) {
    log.debug(prefPopodnevna)
    if (sortKriterijum?.toString() == "Pol") {
        popodnevneSmjene = sortPoPolu(prefPopodnevna, brojPopodnevnihCasova)
    } else {
        popodnevneSmjene = sortPoGodistu(prefPopodnevna, brojPopodnevnihCasova)
    }
    log.debug(popodnevneSmjene)
    log.debug(kreirajSmjeneISubtaskove(popodnevneSmjene, datumVrijemePopodne, "P-"))
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

def kreirajSmjeneISubtaskove(grupaArray, pocetakPrveSmjene, jutarnjaOrPopodnevna) {
    def pocetakSmjene = pocetakPrveSmjene
    def temparray = []
    def slova = ["A","B","C","D","E","F","G","H"]

    grupaArray.eachWithIndex { grupa, i ->
        def slovo = slova[i]
        def vrijemeCasa = pocetakSmjene
        def pocetakStr = pocetakSmjene.toString()
        def year = pocetakStr.split("-")[0]
        def month = pocetakStr.split("-")[1]
        def day = pocetakStr.split("-")[2].split(" ")[0]
        def hour = pocetakStr.split(" ")[1].split(":")[0]
        def minute = pocetakStr.split(" ")[1].split(":")[1]
        def sum = "Grupa $jutarnjaOrPopodnevna$slovo $hour:$minute" + "h" 

        MutableIssue grupaIssue = issueFactory.getIssue()
        grupaIssue.setSummary(sum)
        grupaIssue.setParentObject(issue)
        grupaIssue.setProjectObject(KAN)
        grupaIssue.setIssueTypeId("10700")
        grupaIssue.setAssignee(visol)
        grupaIssue.setReporter(visol)
        grupaIssue.setCustomFieldValue(prisutniKandidatiField, grupa)
        grupaIssue.setCustomFieldValue(datumVrijemeCasField, pocetakSmjene)     
        grupaIssue.setCustomFieldValue(predavacField, predavac)
        grupaIssue.setCustomFieldValue(predavacPPField, predavacPP)
        Map<String,Object> grupaIssueParams = ["issue" : grupaIssue] as Map<String,Object>
        issueManager.createIssueObject(visol, grupaIssueParams)
        linkMgr.createIssueLink(issue.id, grupaIssue.id, 10500, 1, visol)

        kandidatiLinkField.updateValue(null, grupaIssue, new ModifiedValue(grupaIssue.getCustomFieldValue(prisutniKandidatiField), grupa),changeHolder) 
        /*
        brojDana.times {
            pocetakStr = vrijemeCasa.toString()
            year = pocetakStr.split("-")[0]
            month = pocetakStr.split("-")[1]
            day = pocetakStr.split("-")[2].split(" ")[0]
            hour = pocetakStr.split(" ")[1].split(":")[0]
            minute = pocetakStr.split(" ")[1].split(":")[1]
            sum = "\u010cas teorija $day.$month.$year $hour:$minute" + "h" 
            temparray << sum

            MutableIssue casIssue = issueFactory.getIssue()
            casIssue.setSummary(sum)
            casIssue.setParentObject(grupaIssue)
            casIssue.setProjectObject(KAN)
            casIssue.setIssueTypeId("10401")
            //casIssue.setCustomFieldValue(prisutniKandidatiField, grupa)
            casIssue.setCustomFieldValue(datumVrijemeCasField, vrijemeCasa)     
            casIssue.setCustomFieldValue(predavacField, pred)
            casIssue.setCustomFieldValue(predavacPPField, pred)
            Map<String,Object> casIssueParams = ["issue" : casIssue] as Map<String,Object>
            issueManager.createIssueObject(visol, casIssueParams)
            subTaskManager.createSubTaskIssueLink(grupaIssue, casIssue, visol)

            vrijemeCasa += 1 // Sledeci dan
        }       
        */
        pocetakSmjene = new Timestamp(pocetakSmjene.getTime() +  (1000 * 60 * 60)) // + sat vremena
    }
    return temparray
}
