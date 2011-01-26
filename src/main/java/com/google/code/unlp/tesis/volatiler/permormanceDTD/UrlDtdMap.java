package com.google.code.unlp.tesis.volatiler.permormanceDTD;

import java.util.HashMap;
import java.util.Map;

public class UrlDtdMap {

    private final Map<String, String> urlToResource = new HashMap<String, String>();

    public void addDtdUrlToMap(final String dtdLocation, final String fileName) {
	getUrlToResource().put(dtdLocation, fileName);
    }

    public Map<String, String> getUrlToResource() {
	return urlToResource;
    };

    public UrlDtdMap() {
	urlToResource.put("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd",
		"/dtds/xhtml11.dtd");
	urlToResource.put("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd",
		"/dtds/xhtml1-strict.dtd");
	urlToResource.put("http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent",
		"/dtds/xhtml-lat1.ent");
	urlToResource.put("http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent",
		"/dtds/xhtml-symbol.ent");
	urlToResource.put("http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent",
		"/dtds/xhtml-special.ent");
    }

    public String getSystemValue(String systemId) {
	return urlToResource.get(systemId);
    }

}
