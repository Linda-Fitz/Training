def date = getFieldById("customfield_10204")
def fieldDate = date.getValue()
if (!fieldDate){
  def i = new Date().format( 'dd/MMM/yyyy' )
  date.setFormValue(i)
}

def ime = getFieldById("customfield_10107")
def adresa = getFieldById("customfield_10108")
def pib = getFieldById("customfield_10112")

def summary = getFieldById("summary")

def imeKupca = ime.getValue()
def adresaKupca = adresa.getValue()
def pibKupca = pib.getValue()?.toInteger()



if ((imeKupca)&&(adresaKupca)&&(pibKupca)){
    summary.setFormValue("${imeKupca} | ${adresaKupca} | ${pibKupca}")
}
if ((imeKupca)&&(!adresaKupca)&&(pibKupca)){
    summary.setFormValue("${imeKupca} | ${pibKupca}")
}
if ((imeKupca)&&(adresaKupca)&&(!pibKupca)){
    summary.setFormValue("${imeKupca} | ${adresaKupca}")}
