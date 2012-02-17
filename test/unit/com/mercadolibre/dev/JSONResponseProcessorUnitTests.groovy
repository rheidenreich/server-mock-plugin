package com.mercadolibre.dev

import org.codehaus.groovy.grails.web.json.JSONArray

import grails.converters.*
import grails.test.GrailsUnitTestCase

class JSONResponseProcessorUnitTests extends GrailsUnitTestCase{
	
	private static final String ENCODING = "UTF-8"
	private static final int ERROR_STATUS = 404;
	private static final int GET_OK_STATUS = 200;

	private static final String GET_METHOD     = "GET";
	private static final String POST_METHOD    = "POST";
	private static final String PUT_METHOD     = "PUT";
	private static final String DELETE_METHOD  = "DELETE";
	private static final String OPTIONS_METHOD = "OPTIONS";
	
	void testSingleAttributeJSON(){
		
		def map = [:]
		map.id = "123"; 
		
		def resp = JSONResponseProcessor.getResponse(map, GET_METHOD);
		
		assertEquals("123", resp.responseContent.id);
		assertEquals(200, resp.responseCode);
		
		}
	
	void testSeveralAttributesJSON(){

		def map = [:];
		map.id = "ID";
		map.name = "NAME";
		map.group = "GROUP";
		map.code = 100;
		map.attributes = [[id:"A1", name:"N1", code:10],[id:"A2", name:"N2", code:11]]
		
		def resp = JSONResponseProcessor.getResponse(map, GET_METHOD);
		
		assertEquals("NAME", resp.responseContent.name);
		assertEquals(100, resp.responseContent.code);
		assertEquals("A1", resp.responseContent.attributes[0].id)
		assertEquals(11, resp.responseContent.attributes[1].code)
		 
		}
	
	void testWithResponseAndContentError(){
		
		def map = [:]
		map.responseCode = ERROR_STATUS
		map.responseContent = [status:ERROR_STATUS, message:"NOT_FOUND"]
		
		def jsonArray = new JSONArray()
		
		def resp = JSONResponseProcessor.getResponse(map, GET_METHOD);
		
		assertEquals(ERROR_STATUS, resp.responseCode)
		assertEquals(ERROR_STATUS, resp.responseContent.status)
		assertEquals("NOT_FOUND", resp.responseContent.message)
		
		}
	
	/**
	 * Para probar respuestas como la de la api de Atributos
	 */
	void testWithJSONArray(){
		
		def attrs = []
		attrs.add([id:"A1", name:"N1", code:10])
		attrs.add([id:"A2", name:"N2", code:20])				
		
		def resp = JSONResponseProcessor.getResponse(attrs, GET_METHOD);
		
		assertEquals("A1", resp.responseContent[0].id)
		assertEquals(20, resp.responseContent[1].code)

		}
	
	/**
	* Para probar respuestas como la de la api de Atributos con el responseStatus definido
	*/
   void testWithJSONArrayAndDefinedContent(){
	   
	   def attrs = []
	   attrs.add([id:"A1", name:"N1", code:10])
	   attrs.add([id:"A2", name:"N2", code:20])
	   
	   def map = [:]
	   map.responseCode = GET_OK_STATUS
	   map.responseContent = attrs
	   
	   def resp = JSONResponseProcessor.getResponse(attrs, GET_METHOD);
	   
	   assertEquals("A1", resp.responseContent[0].id)
	   assertEquals(20, resp.responseContent[1].code)

	   }

}
