import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.index.IssueIndexingService
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.user.ApplicationUser
def log = Logger.getLogger("com.acme.InstruktorVoziloLinkListener")
log.setLevel(Level.DEBUG)

ApplicationUser visol = ComponentAccessor.getUserManager().getUserByName("VISOL");
ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(visol)
def cfManager = ComponentAccessor.getCustomFieldManager()
def linkMgr = ComponentAccessor.getIssueLinkManager()
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
//Issue issue = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase("CE-1175")

if (issue.getIssueTypeId() == "10101") {  // Promjena na instruktoru
	def voziloPromijenjeno = event?.getChangeLog()?.getRelated("ChildChangeItem").find {it.field == "Vozilo"}
    log.debug(voziloPromijenjeno)
	if(!voziloPromijenjeno){return}

	def staroVozilo = ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(voziloPromijenjeno.oldstring) // Staro vozilo
	def instruktoriField = cfManager.getCustomFieldObject("customfield_10403")
	def instruktoriNaStarom = staroVozilo?.getCustomFieldValue(instruktoriField) // Array instruktora na starom vozilu
	if (instruktoriNaStarom?.contains(issue)) {  // ? je null safe operator
		instruktoriNaStarom.remove(instruktoriNaStarom.indexOf(issue)) // Uklanjanje instruktora iz array-a.
		instruktoriField.updateValue(null, staroVozilo, new ModifiedValue(staroVozilo.getCustomFieldValue(instruktoriField), instruktoriNaStarom), new DefaultIssueChangeHolder())	
		
	}
	if (staroVozilo) {
		issueIndexingService.reIndex(staroVozilo)
	}
	
	// Dodavanje na novom
	def novoVozilo = issue.getCustomFieldValue(cfManager.getCustomFieldObject("customfield_10324"))
	def instruktoriNaNovom = novoVozilo.getCustomFieldValue(instruktoriField)
	if (!instruktoriNaNovom) {
	instruktoriNaNovom = [issue]
		instruktoriField.updateValue(null, novoVozilo, new ModifiedValue(novoVozilo.getCustomFieldValue(instruktoriField), instruktoriNaNovom), new DefaultIssueChangeHolder())
	} else if (!instruktoriNaNovom.contains(issue)) {
		instruktoriNaNovom.add(issue)
		instruktoriField.updateValue(null, novoVozilo, new ModifiedValue(novoVozilo.getCustomFieldValue(instruktoriField), instruktoriNaNovom), new DefaultIssueChangeHolder())
	}
	issueIndexingService.reIndex(novoVozilo)
} else if (issue.getIssueTypeId() == "10100") {
	return // Trenutno nema polja na edit screen-u Vozila
}
