package com.mercadolibre.dev



class MockServerWebTests extends grails.util.WebTest {

    // Unlike unit tests, functional tests are sometimes sequence dependent.
    // Methods starting with 'test' will be run automatically in alphabetical order.
    // If you require a specific sequence, prefix the method name (following 'test') with a sequence
    // e.g. test001XclassNameXListNewDelete

   void testCountriesGET() {
        invoke(url:'http://localhost:8080/server-mock-plugin/mocks/countries/VE')	
		
		verifyText(text:'VE')
		verifyText(text:'Venezuela')
		verifyText(text:'VEF')
    }
   
   void testCategories(){
	   
	   invoke(url:'http://localhost:8080/server-mock-plugin/mocks/categories/MLV53287')
	   
	   verifyText(text:'Integra')	   
	   verifyText(text:'MLV53287')
	   }
   
   void testAttributesWithResponseContentGET(){
	   
	   invoke(url:'http://localhost:8080/server-mock-plugin/mocks/categories/MLV53287/attributes')
	   
	   verifyText(text:'MLV1744-MARC')
	   verifyText(text:'Integra')
	   }
   
   void testAttributesWithoutResponseContentGET(){
	   
	   invoke(url:'http://localhost:8080/server-mock-plugin/mocks/categories/MLV24202/attributes')
	   
	   verifyText(text:'MLV1744-MARC-BMW')
	   verifyText(text:'BMW')
	   
	   }
   
   void testReallyLong1(){
	   
//	   invoque(url: "http://localhost:8080/server-mock-plugin/mocks/auth/user_session/AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB")
	   
	   invoke(url:'http://localhost:8080/server-mock-plugin/mocks/auth/user_session/AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB')
	   
	   verifyText(text: '41768430')
	   
	   }
   
   void testLong1(){
	   
	   invoke(url:'http://localhost:8080/server-mock-plugin/mocks/users?caller.id=132&client.id=144&test1=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test2=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test3=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test4=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test5=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&test6=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')
	   
	   verifyText(text:'NAME')
	   
	   }
   

}