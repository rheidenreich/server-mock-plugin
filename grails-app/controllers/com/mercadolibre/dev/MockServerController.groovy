package com.mercadolibre.dev

import grails.converters.*

import java.io.FileInputStream

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MockServerController {

	private static final String ENCODING = "UTF-8"
	private static final int ERROR_STATUS = 404;

	private static final String GET_METHOD     = "GET";
	private static final String POST_METHOD    = "POST";
	private static final String PUT_METHOD     = "PUT";
	private static final String DELETE_METHOD  = "DELETE";
	private static final String OPTIONS_METHOD = "OPTIONS"; 

	def mockServerConfig
	def mockFileService

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
		try {
			String fileName = getFileName()
			ins = getFileInputStream(fileName, method)
			def resp = JSONResponseProcessor.getResponse(JSON.parse(ins, ENCODING), method)
			log.debug "${method}: Returning JSON fileName: [${fileName}] for Request URI [${getURI()}]. Returning [${resp.responseCode}]."
			render (text: resp.responseContent , status:resp.responseCode)
		} catch (java.io.FileNotFoundException e) {
			String errorMsg = "${method}: Request URI [${getURI()}], JSON Mock file [${fileName}] not found. Returning [${ERROR_STATUS}]."
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: ERROR_STATUS)
		} catch (Exception e) {
			String errorMsg = e.getMessage()
			log.error errorMsg
			render (text: "{\"error\":\"${errorMsg}\"}", status: ERROR_STATUS)
		} finally {
			if (ins) ins.close()
		}
	}

	private String getFileName(){
		SortedMap paramMap = getSortedQueryParams()
		Iterator iterator = paramMap.keySet().iterator();
		StringBuffer queryParamStringBuff = new StringBuffer("")
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (paramMap.get(key) instanceof String){
				queryParamStringBuff.append key
				queryParamStringBuff.append "="
				queryParamStringBuff.append paramMap.get(key)
			}
		}
		String forwardUri = new String(request.forwardURI).replace( request.contextPath, "").replace("/mocks","")
		String folder =  forwardUri.substring(0, forwardUri.indexOf("/",2)+1)
		if (!folder && forwardUri){
			// TODO: en el caso de que sea un /items o similar
			folder = forwardUri+"/"
			}
		String altFileName = forwardUri.replace(folder,"")
		StringBuffer fileName = new StringBuffer(new String(folder+(forwardUri.replaceAll("/", "_"))))
		// Si no tiene queryString
		if (fileName.toString().endsWith("_")){
			fileName.deleteCharAt fileName.lastIndexOf ("_")
		}
		// En caso de tener queryString agrega al filename "?"
		if (!queryParamStringBuff.toString().equals("")){
			fileName.append "?"
		}
		fileName.append queryParamStringBuff.toString()
		if (fileName.toString().length() > 250) {
			// Por limitaciones de OS se convierte a un hash
			fileName = new StringBuffer(MD5Helper.getHash(fileName.toString()))
		}
		fileName.append ".json"
		return fileName.toString()
	}

	private String getAlternativeFileName(String method){
		// replace the existing controller url-map
		String forwardUri = new String(request.forwardURI).replace( request.contextPath, "").replace("/mocks","")
		String folder =  forwardUri.substring(0, forwardUri.indexOf("/",1)+1)
		String altFileName = forwardUri.replace(folder,"")
		StringBuffer fileName = new StringBuffer(new String(folder+(forwardUri.replaceAll("/", "_"))))
		// Si no tiene queryString
		if (fileName.toString().endsWith("_")){
			fileName.deleteCharAt fileName.lastIndexOf ("_")
		}
		while (fileName != null && !fileName.toString().equals("")){
			if (new File(grailsApplication.config.mock.filesource+"/${method}/"+fileName.toString()+"_*.json").exists()) {
				fileName.append "_*.json"
				return fileName.toString()
			}
			fileName.delete(fileName.lastIndexOf ("_"), fileName.length())
		}
		return fileName.toString()
	}

	private SortedMap getSortedQueryParams(){
		SortedMap queryParams = new TreeMap();
		//Ordeno los parametros
		queryParams.putAll(params)
		// Elimino parametros que no son parte de queryString
		queryParams.remove "action"
		queryParams.remove "controller"
		queryParams.remove "caller.admin"
		return queryParams
	}

	private FileInputStream getFileInputStream(String file, String method){
		FileInputStream ins
		try {
			// mockServerConfig grailsApplication.config.mock.filesource mockServerConfig.filePath
			//String home = ConfigurationHolder.config.mock.filesource
			String fullName = "${ConfigurationHolder.config.mock.filesource}/${method}${file}"
			log.debug("getting filename: "+fullName)
			ins = new FileInputStream(fullName)
			return ins
		} catch (java.io.FileNotFoundException e) {
			try {
				String alternativeFileName = getAlternativeFileName(method)
				ins = new FileInputStream("${ConfigurationHolder.config.mock.filesource}/${method}/${alternativeFileName}")
				//				log.warn "${method}: Use optional file [${alternativeFileName}}] for Request URI [${getURI()}]"
				return ins
			} catch (java.io.FileNotFoundException e1) {
				throw e // Throw first
			}
		}
	}

	private String getURI(){
		return request.forwardURI + (request.queryString?"?"+request.queryString:"")
	}
}