package com.mercadolibre.dev

import grails.converters.*
import com.mercadolibre.dev.Constants
import com.sun.org.apache.xalan.internal.xsltc.compiler.Closure

class RestclientMockService {

    static transactional = false
	
	def mockedFileService

    def get = { params -> 
		FileInputStream ins = null
		String fileName
		RestClientResponse resp
		def resultClosure = { closure, inData ->	
			def	data = [status: resp.responseCode, data: resp.responseContent]
			closure.call(data)
			}
		try{
			fileName = mockedFileService.getFileName(params.uri, [:])
			ins = mockedFileService.getFileInputStream(fileName, Constants.GET_METHOD)
			resp = JSONResponseProcessor.getResponse(JSON.parse(ins, Constants.ENCODING), Constants.GET_METHOD)
//			log.debug "${method}: Returning JSON fileName: [${fileName}] for Request URI [${uri}]. Returning [${resp.responseCode}]."
			if (resp.responseCode < 399){
				resultClosure(params.success, resp)
			}else{
				resultClosure(params.failure, resp)
			}
		}
		catch(Exception e){
			resultClosure(params.failure, resp)
			}
		return [status: resp.responseCode, data: resp.responseContent]
    }
	
	def test = { params ->
		
		params.success
		
		return (params.success+"-"+params.failure) 
		
		}
	
	
	
	
}
