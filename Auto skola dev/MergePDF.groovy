import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import org.apache.commons.io.IOUtils
import com.atlassian.jira.util.io.InputStreamConsumer
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.MergePDFAttachments")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("CONF-1")
UserUtil userUtil = ComponentAccessor.getUserUtil()
visol = userUtil.getUserByName("visol")
attachmentManager = ComponentAccessor.getAttachmentManager()

/*
* Import pdf merger klase sa repozitorijuma
* https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox/2.0.13
*/
classLoader = new groovy.lang.GroovyClassLoader()
Map[] grapez = [[group : 'org.apache.pdfbox', module : 'pdfbox', version : '2.0.15']]
groovy.grape.Grape.grab(classLoader: classLoader, grapez)
pdfMerger = classLoader.loadClass("org.apache.pdfbox.multipdf.PDFMergerUtility").newInstance()
PDDocument = classLoader.loadClass("org.apache.pdfbox.pdmodel.PDDocument")
MemoryUsageSetting = classLoader.loadClass("org.apache.pdfbox.io.MemoryUsageSetting")

// Filtriranje PDF fajlova
List<Attachment> attachments = attachmentManager.getAttachments(issue);
log.debug("All attachments: " + attachments.size().toString())

def pdfAttachments = attachments.findAll { it.getMimetype() == "application/pdf" }
log.debug("PDF files: " + pdfAttachments.size().toString())

if (pdfAttachments.size() <= 1) { 
	log.debug('No files to merge')
	return 
}

attachments.each {
	log.debug(it.getMimetype())
	log.debug(it.getProperties())

}

def existing = pdfAttachments.find {it.getFilename() == "${issue.key}.pdf"}
if (existing) {
	log.debug('File already exists')
	return
}

def projectKey = issue.getProjectObject().getKey()
new File("c:/Jira attachments/${projectKey}/").mkdirs()
File mergedFile = new File("c:/Jira attachments/${projectKey}/${issue.key}.pdf")
def path = mergedFile.getAbsolutePath()
mergedFile.createNewFile()
mergedFile.setWritable(true)
try {
	def mergie = ''
    pdfAttachments.eachWithIndex { attachment, i ->
    	def attachmentFileName = attachment.getFilename()
    	mergie += attachmentFileName + ', '
    	def pdfContent = attachmentManager.streamAttachmentContent(attachment, new InputStreamConsumer<byte[]>() {
		    @Override
		    public byte[] withInputStream(final InputStream is) throws IOException
		    {
		    	ByteArrayOutputStream output = new ByteArrayOutputStream();
		    	byte[] buffer = new byte[65536];
	            int l;
	            while ((l = is.read(buffer)) > 0) {
	                output.write(buffer, 0, l);
	            }
            	byte[] pdfBytes = output.toByteArray();
		    	return pdfBytes
		    }
		});
    	new File("c:/Jira attachments/${projectKey}/${issue.key}/").mkdirs()
    	File pdfFile = new File("c:/Jira attachments/${projectKey}/${issue.key}/${attachmentFileName}")
	    pdfFile << pdfContent
		pdfMerger.addSource(pdfFile)
	}
    pdfMerger.setDestinationFileName(path)
    pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly())
    mergie = mergie.substring(0, mergie.size() - 2)
    log.debug("Files ${mergie} have been merged into ${path}")
} catch (IOException e) {
    log.error("Error to merge files. Error: " + e.getMessage());
}

def createAttachmentParamsBean = new CreateAttachmentParamsBean(
	new File(path),
	"${issue.key}.pdf",
	"application/pdf",
	visol,
	issue,
	false,
	false,
	[:],
	new Date(),
	true 
)

if(attachmentManager.createAttachment(createAttachmentParamsBean)) {
	pdfAttachments.each {
		attachmentManager.deleteAttachment(it)
	}
}

