import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ServerMockPluginUrlMappings {

	static mappings = {
		
		"/mocks/**"(controller:'mockServer'){
			action = {
				if (params.method){
					[POST:params.method].get(request.method)
				} else {
					[OPTIONS:'options', DELETE:'delete', GET: 'get', POST: 'post', PUT: 'put'].get(request.method)
				}
			}
		}
	}
}
