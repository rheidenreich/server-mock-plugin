package com.mercadolibre.dev

import grails.test.*

class RestclientMockServiceTests extends GrailsUnitTestCase {
	
	def restService
	
    protected void setUp() {
        super.setUp()
		restService = new RestclientMockService()
		restService.mockedFileService = new MockedFileService()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGETCountries() {
		
		def data
		restService.get(uri: "/countries/VE",
						success: {
							data = it.data
							},
						failure: {
							data = it.data
							}, 
						headers: []									
			)
		assertEquals("VE", data.id)
		assertEquals("Venezuela", data.name)

    }
	
    void testGETCategoriesIntegra() {
		
		def data
		restService.get(uri: "/categories/MLV53287",
						success: {
							data = it.data
							},
						failure: {							
							data = it.data
							}, 
						headers: []									
			)
		assertEquals("MLV53287", data.id)
		assertEquals("Integra", data.name)

    }
	
	
	void testGETAttributesArray() {
		
		def data
		restService.get(uri: "/categories/MLV53287/attributes",
						success: {
							data = it.data
							},
						failure: {
							data = it.data
							},
						headers: []
			)
		
		def attrMarca = data.find { it.id.equals("MLV1744-MARC")}
		def attrModelo = data.find { it.id.equals("MLV1744-MODL")}
		
		assertEquals("Marca", attrMarca.name)
		assertEquals("Modelo", attrModelo.name)

	}
	
}
