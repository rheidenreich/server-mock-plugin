package com.mercadolibre.dev

import grails.test.*
import org.junit.Test
import grails.test.mixin.TestFor

@TestFor(MockedFileService)
class MockedFileServiceTests{


	void testSimpleFileName(){

        String forwardUri = "/countries/VE"
        def paramsMap = [:]

        assertEquals ("/countries/VE.json", service.getFileName(forwardUri, paramsMap))

	}
	
	void testFileNameWithParams(){
		
        String forwardUri = "/countries/VE"
        def paramsMap = ["caller.id":"132", "client.id":"144"]

        assertEquals ("/countries/VE?caller.id=132&client.id=144.json", service.getFileName(forwardUri, paramsMap))

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
		
		StringBuffer fileName = new StringBuffer("users?caller.id=132&client.id=144")
		fileName.append("&test1=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("&test2=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("&test3=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("&test4=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		fileName.append("&test5=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        fileName.append("&test6=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")

		String hashedFileName = MD5Helper.getHash(fileName.toString())
		
			
		assertEquals ("/users/"+hashedFileName+".json", service.getFileName(forwardUri, paramsMap))
	
			}
	

	void testNoParamsUrl(){
		
		String forwardUri = "/countries"
		def paramsMap = [:]
			
		assertEquals ("/countries/countries.json", service.getFileName(forwardUri, paramsMap))
		
		}
	
	
	void testSimpleAlternativeName(){
		
		String forwardUri = "/users"
		def paramsMap = [:]
			
		assertEquals ("/users/*.json", service.getAlternativeFileName("GET",forwardUri))
		
		}
	
	void testSimpleFolderName(){
		String forwardUri = "/countries"		
		assertEquals("/countries/", service.parseFolderName(forwardUri))
		}
	
	void testOldWorldFolderName(){
		String forwardUri = "/jm/webservices/id=123"
		assertEquals("/jm/webservices/", service.parseFolderName(forwardUri))
		}
	
	void testLongURLFolderName(){
		String forwardUri = new String("/users?caller.id=132&client.id=144&test1=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test2=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test3=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test4=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test5=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test6=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
		assertEquals("/users/", service.parseFolderName(forwardUri))
		}
	
	void testSeveralLevelsFolderName(){
		String forwardUri = "/jm/webservices/tucarro/otherlevel/123" 
		assertEquals("/jm/webservices/tucarro/otherlevel/", service.parseFolderName(forwardUri))
		}
	
	
	void testSimpleFileContent(){
		
		String method = "GET"
		String fileName = "/countries/VE.json"
		
		def ins = service.getFileInputStream(fileName, method)
		
		assertTrue( new DataInputStream(ins).readLine().contains("VE"))
		
		assertTrue(true)
		
		}
}
