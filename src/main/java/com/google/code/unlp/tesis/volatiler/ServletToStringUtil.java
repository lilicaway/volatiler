package com.google.code.unlp.tesis.volatiler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletToStringUtil {

    public static String toString(ServletRequest reqToPrint) {
	if (reqToPrint == null) {
	    return "null";
	} else if (reqToPrint instanceof HttpServletRequest) {
	    HttpServletRequest httpReqToPrint = (HttpServletRequest) reqToPrint;
	    return httpReqToPrint.getRequestURL().toString();
	} else {
	    return reqToPrint.toString().replaceAll("\r?\n", "|");
	}
    }
}
