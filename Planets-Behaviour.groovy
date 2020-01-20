
def namePlanet = getFieldById("customfield_10402") //Name of Planet
def diameterPlanet = getFieldById("customfield_10403") // Diameter of Planet
def distancePlanet = getFieldById("customfield_10406") // Distance from Sun
def yearPlanet = getFieldById("customfield_10405") // Length of year

def namePlanetValue = namePlanet.getValue()
def summary = getFieldById("summary")
//Setting Summary from issue type and planet name

def issuetypename = getIssueContext().getIssueTypeObject().getName().toString()
summary.setFormValue("${issuetypename} | ${namePlanetValue} ")
//Mandatory fields = namePlanet -diameterPlanet - distancePlanet- yearPlanet
namePlanet.setRequired(true);
diameterPlanet.setRequired(true);
distancePlanet.setRequired(true);
yearPlanet.setRequired(true);
