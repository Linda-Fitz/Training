import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.sql.Timestamp;
import java.time.LocalDate;

def startDate = getFieldById("customfield_10301")
def nofDays = getFieldById("customfield_10302")
def endDate = getFieldById("customfield_10303")

  def startDateValue = startDate.getValue()
  def nofDaysValue = nofDays.getValue().toInteger()
	//Specifying date format that matches the given date
  def dateFormat=new SimpleDateFormat("dd/MMM/yyyy")
	//Number of Days to add
  def numberTimestamp =  new Timestamp(startDateValue.getTime()) + nofDaysValue
  def dueFormatted =  dateFormat.format(new Date(numberTimestamp.getTime())).toString()
  //Set field End Date
  endDate.setFormValue(dueFormatted)
