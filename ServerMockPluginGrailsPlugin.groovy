import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ServerMockPluginGrailsPlugin {
    // the plugin version
    def version = "0.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
	
	// Lo dejamos habilitado solo para development y test
	def environments = ['development', 'test']
	def scopes = [excludes:'war']
	
//	def loadBefore = ['mlapp']

    // TODO Fill in these fields
    def author = "Roman Babkin & Trigve Korssjoen"
    def authorEmail = "rbabkin10@gmail.com"
    def title = "Static content file-server"
    def description = '''\\
The Main purpose for this plugin is to emulate calls to an external API, being able to manage response status codes.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/server-mock-plugin"
	
	def fileSource

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
		fileSource = '/home/mocks/'
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
		loadConfig()
    }
	
	void loadConfig() {
		GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
		 def confClass
		try {
			 confClass = classLoader.loadClass('MockServerConfig')
		 } catch (Exception e) {
			// <gulp>
		 }
		ConfigObject config = confClass ? new ConfigSlurper().parse(confClass).merge(ConfigurationHolder.config) : ConfigurationHolder.config
		
//		ServerMockPluginGrailsPlugin.fileSource = config.mock.filesource


	}

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
