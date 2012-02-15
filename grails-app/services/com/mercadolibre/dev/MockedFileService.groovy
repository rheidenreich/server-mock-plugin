package com.mercadolibre.dev

import java.io.FileInputStream
import java.util.Map
import java.util.SortedMap

class MockedFileService {

    static transactional = false
	
	//private static final String HOME_FOLDER_PATH = ConfigurationHolder.config.mock.filesource


    public FileInputStream getFileInputStream(String file, String method){ // , String forwardURI, String contextPath, Map params
		FileInputStream ins
		try {
//			String fullName = "${basedir}/grails-app/mocks}/${method}${file}" // ${${basedir}/grails-app/mocks}s
			String fullName = new File("").getAbsolutePath()+"/grails-app/mocks/${method}${file}"
			ins = new FileInputStream(fullName)
			return ins
		} catch (java.io.FileNotFoundException e) {
			log.error("No se pudo encontrar el archivo [${file}]")
			throw e	
		}
	}
	
	
//	public FileInputStream getAlternativeFileInputStream(String file, String method, String forwardURI, String contextPath, Map params){
//		FileInputStream ins
//		try{
//			String alternativeFileName = getAlternativeFileName(method, forwardURI, contextPath)
//			String fullName = new File("").getAbsolutePath()+"/grails-app/mocks/${method}${alternativeFileName}"
//			ins = new FileInputStream("${this.HOME_FOLDER_PATH}/${method}/${alternativeFileName}")
//			return ins
//			}
//		catch(java.io.FileNotFoundException e){
//			log.error("No se pudo encontrar el archivo [${file}]")
//			throw e			
//			}
//		
//	}
	
	
	
	public String getFileName(String forwardURI, String contextPath, Map params){
		SortedMap paramMap = getSortedQueryParams(params)
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
		String forwardUri = new String(forwardURI).replace(contextPath, "").replace("/mocks","")
		String folder =  parseFolderName(forwardUri) 
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
			String hashedFileName = MD5Helper.getHash(fileName.toString())
			fileName = new StringBuffer(folder+hashedFileName)
		}
		fileName.append ".json"
		return fileName.toString()
	}
	
	
	public String getAlternativeFileName(String method, String forwardURI, String contextPath){
		// replace the existing controller url-map
		String forwardUri = new String(forwardURI).replace( contextPath, "").replace("/mocks","")
		String folder = parseFolderName(forwardUri)
		String altFileName = forwardUri.replace(folder,"")
		StringBuffer fileName = new StringBuffer(new String(folder+(forwardUri.replaceAll("/", "_"))))
		// Si no tiene queryString
		if (fileName.toString().endsWith("_")){
			fileName.deleteCharAt fileName.lastIndexOf ("_")
		}
		while (fileName != null && !fileName.toString().equals("")){
//			if (new File(HOME_FOLDER_PATH+"/${method}/"+fileName.toString()+"_*.json").exists()) {
				fileName.append "_*.json"
				return fileName.toString()
//			}
			fileName.delete(fileName.lastIndexOf ("_"), fileName.length())
		}
		return folder + fileName.toString()
	}
	
	protected String parseFolderName(String forwardUri){
		return "/"+forwardUri.split("/")[1]+"/"
		}
	
	
	private SortedMap getSortedQueryParams(Map params){
		SortedMap queryParams = new TreeMap();
		//Ordeno los parametros
		queryParams.putAll(params)
		// Elimino parametros que no son parte de queryString
		queryParams.remove "action"
		queryParams.remove "controller"
		queryParams.remove "caller.admin"
		return queryParams
	}

	private String getURI(String forwardURI, String queryString){
		return forwardURI + (queryString?"?"+queryString:"")
	}
}
