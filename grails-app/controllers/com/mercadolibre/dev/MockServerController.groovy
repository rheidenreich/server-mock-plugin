package com.mercadolibre.dev

import grails.converters.*

import javax.servlet.http.HttpServletResponse


class MockServerController {
 

	def mockServerConfig
	def mockedFileService

	def get = {
		 processRequest(RestMethod.GET.name())
	}

	def put = {
		processRequest(RestMethod.PUT.name())
	}

	def post = {
		 processRequest(RestMethod.POST.name())
	}

	def delete = {
		processRequest(RestMethod.DELETE.name())
	}

	def options = {
		processRequest(RestMethod.OPTIONS.name())
	}

	private def processRequest(String method){
		FileInputStream ins = null
		String fileName
		def resp
		String forwardUri = mockedFileService.cleanForwardURI(request.forwardURI, request.contextPath)
		try {						
			fileName = mockedFileService.getFileName(forwardUri, params)		
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING_UTF8), method)
			log.debug "${method}: Returning JSON fileName: [${fileName}] for Request URI [${getURI()}]. Returning [${resp.responseCode}]."
			render (text: resp.responseContent , status:resp.responseCode)
		} catch (java.io.FileNotFoundException e) {			
			fileName = mockedFileService.getAlternativeFileName(method, forwardUri)
			log.warn "${method}: Use optional file [${fileName}}] for Request URI [${getURI()}]"
			ins = mockedFileService.getFileInputStream(fileName, method)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING_UTF8), method)
			render (text: resp.responseContent , status:resp.responseCode)
		} catch (java.io.FileNotFoundException e){				
			String errorMsg = "${method}: Request URI [${getURI()}], JSON Mock file [${fileName}] not found. Returning [${HttpServletResponse.SC_NOT_FOUND}]."
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: HttpServletResponse.SC_NOT_FOUND)
		} catch (Exception e) {
			String errorMsg = e.getMessage()
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: HttpServletResponse.SC_NOT_FOUND)
		} finally {
			if (ins) ins.close()
		}
	}

	private String getURI(){
		return request.forwardURI + (request.queryString?"?"+request.queryString:"")
	}
}
