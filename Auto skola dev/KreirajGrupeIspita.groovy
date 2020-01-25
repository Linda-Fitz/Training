import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.search.SearchProvider
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
workflowTransitionUtil = ( WorkflowTransitionUtil ) JiraUtils.loadComponent( WorkflowTransitionUtilImpl.class );
workflowTransitionUtil.setUserkey("visol")

//issue = issueManager.getIssueByKeyIgnoreCase("KAN-123")

sad = new Timestamp(System.currentTimeMillis())
prisutniKandidatiField = cfManager.getCustomFieldObject("customfield_10618")
datumVrijemeField = cfManager.getCustomFieldObject("customfield_10624")
brojGrupaField = cfManager.getCustomFieldObject("customfield_11220")
sortKriterijum = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_11104"))
polField = cfManager.getCustomFieldObject("customfield_10423")
datumRodjenjaField = cfManager.getCustomFieldObject("customfield_10134")
ispitField = cfManager.getCustomFieldObject("customfield_10814")
grupaIspitField = cfManager.getCustomFieldObject("customfield_11221")

def kandidati = []
for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
	if (link.getLinkTypeId() == 10301 || link.getLinkTypeId() == 10401) {
		kandidati << link.getSourceObject()	

		link.getSourceObject().getSubTaskObjects().each { subtask ->
			if (subtask.getCustomFieldValue(ispitField) == issue && subtask.getStatusId() == "10406") {
				workflowTransitionUtil.setAction (71) // ispit -> na spisku
				workflowTransitionUtil.setIssue(subtask)
				workflowTransitionUtil.validate()
				workflowTransitionUtil.progress()			
			}
		}	
	} else if (link.getLinkTypeId() == 10300) {
		// Nazad u status koji je bio
		def kandidatNijeUplatioIssue = link.getSourceObject()
		if (kandidatNijeUplatioIssue.getStatusId() == "10303") {
			Boolean padaoIspit
			def prijavljeniIspiti = kandidatNijeUplatioIssue.getSubTaskObjects().findAll { it.getIssueTypeId() == "10405" && it.getStatusId() == "10409" }
			prijavljeniIspiti.each {
				if (it.getStatusId() == "10409") { padaoIspit = true}
			}
			if (padaoIspit) {
				workflowTransitionUtil.setAction (111)			
			}
			workflowTransitionUtil.setIssue(kandidatNijeUplatioIssue)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()				
		}

		// Subtask status u odustao
		def subtask = kandidatNijeUplatioIssue.getSubTaskObjects().find{ it.getCustomFieldValue(ispitField) == issue }
		
		if (subtask.getStatusId() == "10404") { // Prijavljen
			workflowTransitionUtil.setAction(11) // -> Nije uplaceno
			workflowTransitionUtil.setIssue(subtask)
			workflowTransitionUtil.validate()
			workflowTransitionUtil.progress()				
		}
		// Brisanje linka
		linkMgr.removeIssueLink(link, visol)
	}
}

def grupe = []
def brojGrupa = issue.getCustomFieldValue(brojGrupaField)
if (brojGrupa == 1) {
	grupe << kandidati
} else if (brojGrupa > 1) {
	def brojKandidata = kandidati.size()
	if (!sortKriterijum) {
		brojGrupa.times {
			def startrange = Math.ceil(it * brojKandidata / brojGrupa).toInteger()
			def endrange = Math.ceil((it + 1) * brojKandidata / brojGrupa).toInteger()
			def grupaArray = []
			for (i = startrange; i < endrange; i++) {
				grupaArray << kandidati[i]
			}
			grupe << grupaArray
		}
	} else if (sortKriterijum.getOptionId() == 11800) { //pol
		polMusko = []
		polZensko = []
		polUndefined = []
		kandidati.each { kandidat ->
			if(kandidat.getCustomFieldValue(polField)?.toString() == "Muško") {
				polMusko << kandidat
			} else if(kandidat.getCustomFieldValue(polField)?.toString() == "Žensko") {
				polZensko << kandidat
			} else {
				polUndefined << kandidat
			}			
		}
		polUndefined.each { it ->
			if (polMusko.size() > polZensko.size()) {
				polZensko << it
			} else {
				polMusko << it
			}
		}	

		def kandidataPoGrupi = Math.ceil((polMusko.size() + polZensko.size()) / brojGrupa)
		brojGrupa.times { brojGrupe ->
			def grupaArray = []
			kandidataPoGrupi.times { kandidatIndex ->
				if (polMusko.size() > 0) {
					grupaArray << polMusko[0]
					polMusko.remove(0)
				} else if (polZensko.size() > 0){
					grupaArray << polZensko[0]
					polZensko.remove(0)
				}
			}
			grupe << grupaArray
		}
	} else if (sortKriterijum.getOptionId() == 11801) { //godiste
		kandidati.sort { it.getCustomFieldValue(datumRodjenjaField) }

		brojGrupa.times {
			def startrange = Math.ceil(it * brojKandidata / brojGrupa).toInteger()
			def endrange = Math.ceil((it + 1) * brojKandidata / brojGrupa).toInteger()
			def grupaArray = []
			for (i = startrange; i < endrange; i++) {
				grupaArray << kandidati[i]
			}
			grupe << grupaArray
		}
	}	
}

def pocetakGrupe = issue.getCustomFieldValue(datumVrijemeField)
grupe.eachWithIndex { grupa, i ->
	def pocetakStr = pocetakGrupe.toString()
	def year = pocetakStr.split("-")[0]
	def month = pocetakStr.split("-")[1]
	def day = pocetakStr.split("-")[2].split(" ")[0]
	def hour = pocetakStr.split(" ")[1].split(":")[0]
	def minute = pocetakStr.split(" ")[1].split(":")[1]
	def sum = "Grupa $hour:$minute" + "h" 

	MutableIssue grupaIssue = issueFactory.getIssue()
	grupaIssue.setSummary(sum)
	grupaIssue.setProjectObject(KAN)
	grupaIssue.setIssueTypeId("10701")
	grupaIssue.setCustomFieldValue(datumVrijemeField, pocetakGrupe)
	Map<String,Object> grupaIssueParams = ["issue" : grupaIssue] as Map<String,Object>
	issueManager.createIssueObject(visol, grupaIssueParams)
	linkMgr.createIssueLink(issue.id, grupaIssue.id, 10501, i + 1, visol)
	grupa.each { kandidat ->
		linkMgr.createIssueLink(kandidat.id, grupaIssue.id, 10502, 2, visol)
		grupaIspitField.updateValue(null, kandidat, new ModifiedValue(kandidat.getCustomFieldValue(grupaIspitField),grupaIssue ), new DefaultIssueChangeHolder())
	}

	pocetakGrupe = new Timestamp(pocetakGrupe.getTime() +  (1000 * 60 * 60))
}

// Broj prijavljenih 
def brojPrijavljenihField = cfManager.getCustomFieldObject("customfield_11262")
def brojPrijavljenih = linkMgr.getInwardLinks(issue.id).size()
brojPrijavljenihField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(brojPrijavljenihField), (double)brojPrijavljenih), new DefaultIssueChangeHolder())


