package com.mercadolibre.dev

import grails.converters.*

import java.io.FileInputStream
import com.mercadolibre.dev.Constants


class MockServerController {
 

	def mockServerConfig
	def mockedFileService

	def get = {
		 processRequest(RestMethod.GET.toString()) 
	}

	def put = {
		processRequest(RestMethod.PUT.toString()) 
	}

	def post = {
		 processRequest(RestMethod.POST.toString()) 
	}

	def delete = {
		processRequest(RestMethod.DELETE.toString()) 
	}

	def options = {
		processRequest(RestMethod.OPTIONS.toString()) 
	}

	private def processRequest(String method){
		FileInputStream ins = null
		String fileName
		def resp
		String forwardUri = mockedFileService.cleanForwardURI(request.forwardURI, request.contextPath)
		try {						
			fileName = mockedFileService.getFileName(forwardUri, params)		
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING), method)
			log.debug "${method}: Returning JSON fileName: [${fileName}] for Request URI [${getURI()}]. Returning [${resp.responseCode}]."
			render (text: resp.responseContent , status:resp.responseCode)
		} catch (java.io.FileNotFoundException e) {			
			fileName = mockedFileService.getAlternativeFileName(method, forwardUri)
			log.warn "${method}: Use optional file [${fileName}}] for Request URI [${getURI()}]"
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING), method)
			render (text: resp.responseContent , status:resp.responseCode)
		} catch (java.io.FileNotFoundException e){				
			String errorMsg = "${method}: Request URI [${getURI()}], JSON Mock file [${fileName}] not found. Returning [${Constants.ERROR_STATUS}]."
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: Constants.ERROR_STATUS)
		} catch (Exception e) {
			String errorMsg = e.getMessage()
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: Constants.ERROR_STATUS)
		} finally {
			if (ins) ins.close()
		}
	}

	private String getURI(){
		return request.forwardURI + (request.queryString?"?"+request.queryString:"")
	}
}
