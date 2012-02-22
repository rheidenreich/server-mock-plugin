package com.mercadolibre.dev

/**
 * Este servicio se encargara de crear el archivo mock correspondiente a la llamada.
 * 
 * @author fkorssjoen
 *
 */
class FileDumperService {

    static transactional = true

    def createMock(String method, String url) {
		
		withHttp(uri: "http://www.google.com") {
			def html = get(path : '/search', query : [q:'Groovy'])
			assert html.HEAD.size() == 1
			assert html.BODY.size() == 1
		 }
		
		def html = result.get()
		log.info(html.toString())

    }
}
