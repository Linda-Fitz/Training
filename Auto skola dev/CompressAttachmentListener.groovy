import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.issue.index.IssueIndexingService
import static java.awt.RenderingHints.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils
import com.atlassian.jira.util.io.InputStreamConsumer
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.AttachmentCompressListener")
log.setLevel(Level.DEBUG)

def issueManager = ComponentAccessor.getIssueManager()
//Issue issue = issueManager.getIssueByKeyIgnoreCase("UPIT-50")
def issueType = issue.getIssueTypeId()
// Kandidat, Kondicioni, Zaposleni, Vozilo
log.debug("IssueType $issueType")
if (issueType != "10200" && issueType != "10600" && issueType != "10101" && issueType != "10100") { return } 

UserUtil userUtil = ComponentAccessor.getUserUtil()
visol = userUtil.getUserByName("visol")
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
attachmentManager = ComponentAccessor.getAttachmentManager()

def targetSize = (issueType == "10100" || issueType == "10101") ? 600 : 200 // Vodila tlita, otalo to pedetet

List<Attachment> attachments = attachmentManager.getAttachments(issue);
log.debug("All attachments: " + attachments.size().toString())
def images = attachments.findAll { it.getMimetype().contains("image/") }
log.debug("Images: " + images.size().toString())

if (images.find { it.getFilename().contains('Droid-') }) { return }

images.each {
	def attachmentProperties = [:]
	def fileSize = it.getFilesize()
	log.debug("File: " + it.getFilename())
	log.debug("Initial fileSize: " + fileSize.toString())

	log.debug("start")
	log.debug("Filesize: " + fileSize.toString())
	def img = ComponentAccessor.getAttachmentManager().streamAttachmentContent(it, new InputStreamConsumer<BufferedImage>() {
        @Override
        public BufferedImage withInputStream(final InputStream is) throws IOException
        {
        	return ImageIO.read(is)
        }
    });

	def scale = 1 
	if (img.width > img.height) {
		scale = targetSize / img.width
	} else {
		scale = targetSize / img.height		
	}
	if (scale > 1) { 
		scale = 1
	}

	int newWidth = img.width * scale
	int newHeight = img.height * scale

	new File('C:/tmp/scaled.png').mkdirs()
	File tempFile = new File('C:/tmp/scaled.png')
	new BufferedImage( newWidth, newHeight, img.type ).with { i ->
	  createGraphics().with {
	    setRenderingHint( KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC )
	    drawImage( img, 0, 0, newWidth, newHeight, null )
	    dispose()
	  }
	  ImageIO.write(i, 'png', tempFile)
	}

	def createAttachmentParamsBean = new CreateAttachmentParamsBean(tempFile,
		"Droid-" + it.getFilename(),
		it.getMimetype(),
		visol,
		issue,
		false,
		true,
		attachmentProperties,
		new Date(),
		true
	)

	def newAttachmentId = attachmentManager.createAttachment(createAttachmentParamsBean).to
	//attachmentManager.deleteAttachment(it)
}
	
