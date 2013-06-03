package com.mercadolibre.dev

import java.io.FileInputStream
import java.util.Map
import java.util.SortedMap

class MockedFileService {

    static transactional = false

    String separator = File.separator
    private static String GRAILS_FOLDER = "grails-app"
    private static String MOCKS_FOLDER = "mocks"
    private static String JSON_EXTENSION = ".json"


    public FileInputStream getFileInputStream(String file, String method){
		FileInputStream ins
		try {
			String fullName = new File("").getAbsolutePath() + separator + GRAILS_FOLDER + separator + MOCKS_FOLDER + separator + method + file
			ins = new FileInputStream(fullName)
			return ins
		} catch (java.io.FileNotFoundException e) {
			log.error("No se pudo encontrar el archivo [${file}]")
			throw e	
		}
	}
	
	
	public String getFileName(String forwardUri, Map params){
		SortedMap paramMap = getSortedQueryParams(params)
		String queryParams = parseQueryParams(paramMap)
		String folder =  parseFolderName(forwardUri)
        String fileName = forwardUri.substring( forwardUri.lastIndexOf("/") +1)

		String fullFileName = folder + fileName + queryParams + JSON_EXTENSION
		if (fullFileName.length() > 250 ){
            String hashedFileName = MD5Helper.getHash(fileName+queryParams)
            fullFileName = folder + hashedFileName + JSON_EXTENSION
        }
        return fullFileName
	}



    public String parseQueryParams(def paramMap){
        StringBuffer queryParamStringBuff = new StringBuffer("")
        Iterator iterator = paramMap.keySet().iterator();
        if(paramMap.size() > 0){
           queryParamStringBuff.append("?")
        }
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (paramMap.get(key) instanceof String){
                queryParamStringBuff.append key
                queryParamStringBuff.append "="
                queryParamStringBuff.append paramMap.get(key)
                queryParamStringBuff.append "&"
            }
        }
        if (queryParamStringBuff.toString().endsWith("&")){
            queryParamStringBuff.deleteCharAt queryParamStringBuff.lastIndexOf ("&")
        }
        return queryParamStringBuff.toString()
    }
	
	
	public String getAlternativeFileName(String method, String forwardUri){
		String folder = parseFolderName(forwardUri)
		return folder + "*" + JSON_EXTENSION
	}
	
	public String parseFolderName(String forwardUri){
        String folderName
        if (forwardUri.lastIndexOf("/") > 0){
           folderName = forwardUri.substring(0, forwardUri.lastIndexOf("/"))
        }else{
           if(forwardUri.indexOf("?") > 0){
               folderName = forwardUri.substring(0, forwardUri.indexOf("?"))
           }else{
               folderName = forwardUri
           }
        }
	    return folderName + separator
    }
	
	public String cleanForwardURI(String forwardURI, String contextPath){
			return new String(forwardURI).replace( contextPath, "").replace(separator + MOCKS_FOLDER ,"")
		}
	
	
	private SortedMap getSortedQueryParams(Map params){
		SortedMap queryParams = new TreeMap();
		queryParams.putAll(params)
		queryParams.remove "action"
		queryParams.remove "controller"
		queryParams.remove "caller.admin"
		return queryParams
	}
}
