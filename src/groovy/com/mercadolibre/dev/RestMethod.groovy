package com.mercadolibre.dev
public enum RestMethod {
    GET('GET'),
    POST('POST'),
    PUT('PUT'),
    DELETE('DELETE'),
    OPTIONS('OPTIONS')

	private final String value
    
    private RestMethod(String val) {
        value = val
    }
    
    public String toString() {
        return value
    }
}
