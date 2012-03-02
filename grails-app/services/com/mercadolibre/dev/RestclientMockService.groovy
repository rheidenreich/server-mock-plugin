package com.mercadolibre.dev

import grails.converters.JSON


import com.mercadolibre.dev.RestClientResponse
import grails.converters.*
import com.mercadolibre.dev.Constants
import com.sun.org.apache.xalan.internal.xsltc.compiler.Closure

class RestclientMockService {

    static transactional = false
	
	def mockedFileService = new MockedFileService()


    def get = { params -> 
		return processRequest(Constants.GET_METHOD, params)
    }
	
	def post = { params ->
		return processRequest(Constants.POST_METHOD, params)
		}
	

	private def processRequest = {method,  params ->
		
		FileInputStream ins = null
		String fileName
		RestClientResponse resp
		def uri = cleanUri(params.uri)
		def map = getParamMapFromUri(params.uri)
		def resultClosure = { closure, inData ->
			def	data = [status: resp.responseCode, data: resp.responseContent]
			closure.call(data)
			}
		
		try{
			fileName = mockedFileService.getFileName(uri, map)
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING), method)
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
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING),method)
			if (resp.responseCode < 399){
				resultClosure(params.success, resp)
			}else{
				resultClosure(params.failure, resp)
			}
		}
		catch(java.io.FileNotFoundException e1){
			String errorMsg = "${method}: Request URI [${params.uri}], JSON Mock file [${fileName}] not found. Returning [${Constants.ERROR_STATUS}]."
			log.error errorMsg
			resp = [responseContent : "{\"error\":\"${errorMsg}\"}", responseCode: Constants.ERROR_STATUS]
			}
		finally {
			if (ins) ins.close()
		}
		return [status: resp.responseCode, data: resp.responseContent]
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
