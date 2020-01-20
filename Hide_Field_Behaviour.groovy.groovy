def selectField = getFieldById("customfield_10400")
def textField = getFieldById("customfield_10401")

def selectFieldValue = selectField.getOptionId()

if (selectFieldValue == "Yes") {
    textField.setHidden(true)
} else {
    textField.setHidden(false)
}
