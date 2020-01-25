def log(Script script, String message) {
	new File('C:/Logger').mkdirs()
	def logFileName = script.getClass().getSimpleName()
	def logFile = new File("C:/Logger/${logFileName}")
	def time = "${new Date().toString()}: "
	logFile << time << message << "\n"
}