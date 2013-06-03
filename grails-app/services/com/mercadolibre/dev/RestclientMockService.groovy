package com.mercadolibre.dev

import grails.converters.JSON

import com.mercadolibre.opensource.frameworks.restclient.cache.HttpResponseWrapper
import org.apache.http.message.BasicStatusLine
import org.apache.http.ProtocolVersion
import javax.servlet.http.HttpServletResponse

class RestclientMockService {

    static transactional = false

    boolean fromUnitTest = false

	def mockedFileService = new MockedFileService()

    public RestclientMockService(boolean fromUnitTest)
    {
        this.fromUnitTest = fromUnitTest
    }

    public RestclientMockService()
    {
    }

    def get = { params -> 
		return processRequest(RestMethod.GET.name(), params)
    }
	
	def post = { params ->
		return processRequest(RestMethod.POST.name(), params)
		}

    def put = { params ->
        return processRequest(RestMethod.PUT.name(), params)
    }


    private def processRequest = {method,  params ->
		
		FileInputStream ins = null
		String fileName
		RestClientResponse resp
		def uri = cleanUri(params.uri)
		def map = getParamMapFromUri(params.uri)
		def resultClosure = { closure, inData ->
            def data
            if (fromUnitTest)
            {
                data = new HttpResponseWrapper(resp.responseContent, 5,0, mockStatus(resp.responseCode), null)
            }
            else
            {
                data = [status: resp.responseCode, data: resp.responseContent]
            }
			closure.call(data)
			}
		
		try{
			fileName = mockedFileService.getFileName(uri, map)
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING_UTF8), method)
			log.debug "${method}: Returning JSON fileName: [${fileName}] for Request URI [${uri}]. Returning [${resp.responseCode}]."
			if (resp.responseCode < 399){
				resultClosure(params.success, resp)
			}else{
				resultClosure(params.failure, resp)
			}
		}
		catch(java.io.FileNotFoundException e){
			String alternativeFile = mockedFileService.getAlternativeFileName(method, uri)
			String errorMsg = "${method}: Request URI [${uri}], JSON Mock file [${fileName}] not found. Returning ${alternativeFile}"
			log.error(errorMsg)
			fileName = alternativeFile
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING_UTF8),method)
			if (resp.responseCode < 399){
				resultClosure(params.success, resp)
			}else{
				resultClosure(params.failure, resp)
			}
		}
		catch(java.io.FileNotFoundException e1){
			String errorMsg = "${method}: Request URI [${params.uri}], JSON Mock file [${fileName}] not found. Returning [${HttpServletResponse.SC_NOT_FOUND}]."
			log.error errorMsg
			resp = [responseContent : "{\"error\":\"${errorMsg}\"}", responseCode: HttpServletResponse.SC_NOT_FOUND]
			}
		finally {
			if (ins) ins.close()
		}
		return [status: resp.responseCode, data: resp.responseContent]
	}

    private mockStatus(def status)
    {
        return new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), status, status.toString())
    }

	public Map getParamMapFromUri(String uri){
		Map params = [:]
		String splittedParams
		try{
			splittedParams = uri.split("\\?")[1]
		}catch(IndexOutOfBoundsException e){
			splittedParams = null
		}		
		if (splittedParams){
			def separated = splittedParams.split("&")
			separated.each{
				def keyValue = it.split("=")
				params.put(keyValue[0], keyValue[1])
				}			
			}
		return params
	}
	
	
	public String cleanUri(String uri){	
		try{
			return uri.split("\\?")[0]
		}catch(IndexOutOfBoundsException e){
			return uri
		}
		
	} 
	
	
	
	
}

