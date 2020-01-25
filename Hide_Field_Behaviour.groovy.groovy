def selectField = getFieldById("customfield_10400")
def textField = getFieldById("customfield_10401")

def selectFieldValue = selectField.getOptionId()

LazyLoadedOption selectCFVale = issue.getCustomFieldValue(nekiSelectCF)
getOptionId()

if (selectFieldValue == "Yes") {
    textField.setHidden(true)
} else {
    textField.setHidden(false)
}

issue.getCustomFieldValue(CustomField customField)
  text -> String
  multiline text -> String
  select -> LazyLoadedOption
  number -> Double
  issue picker -> Issue
  date/datetime -> Timestamp
