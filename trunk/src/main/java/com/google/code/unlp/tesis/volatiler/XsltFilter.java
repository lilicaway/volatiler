package com.google.code.unlp.tesis.volatiler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class XsltFilter implements Filter {
    private FilterConfig filterConfig = null;
    private static final Logger log = Logger.getLogger(XsltFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
	this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	    ServletException {
	log.warning("Applying XsltFilter on " + request.toString().replaceAll("\r?\n", "|"));
	String contentType;
	String styleSheet;
	contentType = "text/html";
	styleSheet = "/WEB-INF/xml/html.xsl";
	// String type = request.getParameter("type");
	// if (type == null || type.equals("")) {
	// contentType = "text/html";
	// styleSheet = "/xml/html.xsl";
	// } else {
	// if (type.equals("xml")) {
	// contentType = "text/plain";
	// styleSheet = "/xml/xml.xsl";
	// } else {
	// contentType = "text/html";
	// styleSheet = "/xml/html.xsl";
	// }
	// }
	response.setContentType(contentType);
	String stylePath = filterConfig.getServletContext().getRealPath(styleSheet);
	Source styleSource = new StreamSource(stylePath);

	PrintWriter out = response.getWriter();
	CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
	chain.doFilter(request, responseWrapper);
	// Get response from servlet
	StringReader sr = new StringReader(new String(responseWrapper.toString()));
	Source xmlSource = new StreamSource(sr);

	try {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer(styleSource);
	    CharArrayWriter caw = new CharArrayWriter();
	    StreamResult result = new StreamResult(caw);
	    transformer.transform(xmlSource, result);
	    // response.setContentLength(caw.toString().length());
	    out.write(caw.toString());
	} catch (Exception ex) {
	    out.println(ex.toString());
	    out.write(responseWrapper.toString());
	}

	log.warning("Finishing Applying XsltFilter on " + request);
    }

    public FilterConfig getFilterConfig() {
	return filterConfig;
    }
}