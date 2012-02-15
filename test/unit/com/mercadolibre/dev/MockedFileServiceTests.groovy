package com.mercadolibre.dev

import grails.test.*

class MockedFileServiceTests extends GrailsUnitTestCase {
	def fileService
	
    protected void setUp() {
        super.setUp()
		fileService = new MockedFileService()
    }

    protected void tearDown() {
        super.tearDown()
    }

	
	void testSimpleFileName(){
		
	String forwardUri = "/countries/VE"
	def paramsMap = [:]
	String context = ""	
		
	assertEquals ("/countries/_countries_VE.json", fileService.getFileName(forwardUri, context, paramsMap))	

		}
	
	void testFileNameWithParams(){
		
	String forwardUri = "/countries/VE"
	def paramsMap = ["caller.id":"132", "client.id":"144"]
	String context = ""
		
	assertEquals ("/countries/_countries_VE?caller.id=132client.id=144.json", fileService.getFileName(forwardUri, context, paramsMap))

		}
	
	
	void testFileWithLotsOfParams(){
		
		String forwardUri = "/users"
		def paramsMap = ["caller.id":"132", 
			"client.id":"144", 
			"test1":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 
			"test2":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 
			"test3":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
			"test4":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
			"test5":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
			"test6":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
			]
		String context = ""
		
		StringBuffer fileName = new StringBuffer("/users/_users?caller.id=132client.id=144")
		fileName.append("test1=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("test2=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("test3=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("test4=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("test5=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("test6=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		
		String hashedFileName = MD5Helper.getHash(fileName.toString())
		
			
		assertEquals ("/users/"+hashedFileName+".json", fileService.getFileName(forwardUri, context, paramsMap))
	
			}
	
	
	void testNoParamsUrl(){
		
		String forwardUri = "/countries"
		def paramsMap = [:]
		String context = ""
			
		assertEquals ("/countries/_countries.json", fileService.getFileName(forwardUri, context, paramsMap))
		
		}
	
	
	void testSimpleAlternativeName(){
		
		String forwardUri = "/countries"
		def paramsMap = [:]
		String context = ""
			
		assertEquals ("/countries/_countries_*.json", fileService.getAlternativeFileName("GET",forwardUri, context))
		
		}
	
	void testSimpleFolderName(){
		String forwardUri = "/countries"		
		assertEquals("/countries/", fileService.parseFolderName(forwardUri))
		}
	
	void testOldWorldFolderName(){
		String forwardUri = "/jm/webservices/id=123"
		assertEquals("/jm/", fileService.parseFolderName(forwardUri))
		}
	
	void testSeveralLevelsFolderName(){
		String forwardUri = "/jm/webservices/tucarro/otherlevel/123" 
		assertEquals("/jm/", fileService.parseFolderName(forwardUri))
		}
	
	
	void testSimpleFileContent(){
		
		String method = "GET"
		String fileName = "/countries/_countries_VE.json"
		
		def ins = fileService.getFileInputStream(fileName, method)
		
		assertTrue( new DataInputStream(ins).readLine().contains("VE"))
		
		assertTrue(true)
		
		}
}
