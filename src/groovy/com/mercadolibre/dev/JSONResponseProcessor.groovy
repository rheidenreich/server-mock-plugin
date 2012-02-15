package com.mercadolibre.dev


public class JSONResponseProcessor {
    public static RestClientResponse getResponse(json, method) {
		def resp = new RestClientResponse()
		//HTTP Code
		int responseCode;
		try{
			responseCode = json.responseCode
		}catch(Exception e){
			responseCode = 0
		}
		if (json.responseCode && responseCode > 0) {
			resp.responseCode = json.responseCode
		} else  {
			// By default
			resp.responseCode = JSONResponseProcessor.getSuccessStatus(method)
		}
		//Content
		if (json.responseContent && isValidJSON(json.responseContent)){
			resp.responseContent = json.responseContent
		} else {
			resp.responseContent = json
		}
		return resp
    }
	
	private static int getSuccessStatus(String method) {
		int result
		switch ( method ) {
			case RestMethod.GET.toString():
				result = 200
				break
			case RestMethod.PUT.toString():
				result = 200
				break
			case RestMethod.POST.toString():
				result = 201
				break
			case RestMethod.DELETE.toString():
				result = 200
				break
			case RestMethod.OPTIONS.toString():
				result = 200
				break
			default:
				result = 200
		}
		return result
	}
	
	private static boolean isValidJSON(def json){
		boolean b = false 
		json.each{
			if (it!=null){
				b=true
				}
			}
		return b
		} 

}