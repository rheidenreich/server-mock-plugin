package com.mercadolibre.dev

import grails.test.*

import java.util.logging.Level
import java.util.logging.LogManager

import org.apache.log4j.BasicConfigurator

class RestclientMockServiceTests extends GrailsUnitTestCase {
	
	def restclientMockService
	def log
	
    protected void setUp() {
        super.setUp()
		
		mockLogging(RestclientMockService, true)
		mockLogging(MockedFileService, true)
		
		restclientMockService = new RestclientMockService()
		restclientMockService.mockedFileService = new MockedFileService()

		
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGETCountries() {
		
		def data
		restclientMockService.get(uri: "/countries/VE",
						success: {
							data = it.data
							},
						failure: {
							}, 
						headers: []									
			)
		assertEquals("VE", data.id)
		assertEquals("Venezuela", data.name)

    }
	
    void testGETCategoriesIntegra() {
		
		def data
		restclientMockService.get(uri: "/categories/MLV53287",
						success: {
							data = it.data
							},
						failure: {							
							}, 
						headers: []									
			)
		assertEquals("MLV53287", data.id)
		assertEquals("Integra", data.name)

    }
	
	
	void testGETAttributesArray() {
		
		def data
		restclientMockService.get(uri: "/categories/MLV53287/attributes",
						success: {
							data = it.data
							},
						failure: {
							},
						headers: []
			)
		
		def attrMarca = data.find { it.id.equals("MLV1744-MARC")}
		def attrModelo = data.find { it.id.equals("MLV1744-MODL")}
		
		assertEquals("Marca", attrMarca.name)
		assertEquals("Modelo", attrModelo.name)

	}
	
	void testLongURL(){
		
		String uri = new String("/users?caller.id=132&client.id=144&test1=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test2=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test3=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test4=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test5=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test6=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		
		def data
		restclientMockService.get(uri: uri.toString(),
						success: {
							data = it.data
							},
						failure: {
							},
						headers: []
			)
		
		assertEquals("NAME", data.name)
		}
	
	void testLongUrlNoParams(){
		
		String uri = "/auth/user_session/AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"
		
		def data
		restclientMockService.get(uri: uri.toString(),
						success: {
							data = it.data
							},
						failure: {
							},
						headers: []
			)
		
		assertEquals("41768430", data.user_id)
		
		}
	
	/* Help functions */
	
		void testParamsMapOneParam(){
		def uri = "/users?qw=11"
		
		def map = restclientMockService.getParamMapFromUri(uri)
		
		assertEquals(1, map.size())
		
		}
	
		void testParamsMapNoParam(){
			def uri = "/users?"
			
			def map = restclientMockService.getParamMapFromUri(uri)
			
			assertEquals(0, map.size())
			
			}
		
		void testParamsMapSeveralNodes(){
			def uri = "/users?firstParam=firstVal&secundParam=SecundVal&thirdParam=thirdVal&fourthParam=fourthVal"
			
			def map = restclientMockService.getParamMapFromUri(uri)
			
			assertEquals(4, map.size())
			String resultUri = ""
			map.each {
				resultUri += it.key + "=" +it.value
			}
			
			assertEquals(resultUri, "firstParam=firstValsecundParam=SecundValthirdParam=thirdValfourthParam=fourthVal")
		}
		
		
		void testCleanUriWithLotsOfParams(){
			def uri = "/users?firstParam=firstVal&secundParam=SecundVal&thirdParam=thirdVal&fourthParam=fourthVal"			
			def cleaned = restclientMockService.cleanUri(uri)			
			assertEquals("/users", cleaned)
			}
		
		
		void testCleanUriWithNoParams(){
			def uri = "/users"		
			def cleaned = restclientMockService.cleanUri(uri)			
			assertEquals("/users", cleaned)
			
			uri = "/users?"
			cleaned = restclientMockService.cleanUri(uri)
			assertEquals("/users", cleaned)
			
			}
		/* END - Help functions */
		
		/* POST */
		
		
		void testItemPost(){
			
			String uri = "/items?caller.id=41768430&client.id=1443"
			
			def data
			restclientMockService.post(uri: uri.toString(),
							success: {
								data = it
								},
							failure: {
								},
							headers: []
				)
			
			assertEquals("1234567", data.data.id)
			assertEquals(201, data.status)
			
			}
		
		void testItemDeleted() {
			String uri = "/items?caller.id=41768430&client.id=1443"
			def data
			restclientMockService.delete(
					uri: uri.toString(),
					success: {
						data = it
					},
					failure: {
					}
				)
			
			assertEquals(200, data.status)
			assertEquals("deleted", data.data.status)
		}

}
