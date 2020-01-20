import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.sql.Timestamp;
import java.time.LocalDate;

def simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
def datumljField = getFieldById("customfield_10212")
def datumlj = datumljField.getValue()

datumlj = simpleDateFormat.format(datumlj)

def nowFormatted = datumlj.toString()

def year = nowFormatted.split("/")[2].toInteger()
year = (year + 3).toString()
def day = nowFormatted.split("/")[0].toInteger()
day = day.toString()
def month = nowFormatted.split("/")[1].toInteger()
month = month.toString()
String godina = "$day/$month/$year"
Date date1 = simpleDateFormat.parse(godina);
def dat = simpleDateFormat.format(date1)
datumljIstekaField.setFormValue(dat)
// Test commit
