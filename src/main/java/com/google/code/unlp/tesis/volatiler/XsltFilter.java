package com.google.code.unlp.tesis.volatiler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Level;
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

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String XSL_PATH = "xslPath";

    private FilterConfig filterConfig = null;
    private static final Logger log = Logger.getLogger(XsltFilter.class.getName());

    private String xslPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	this.filterConfig = filterConfig;
	xslPath = filterConfig.getInitParameter(XSL_PATH);
	if (xslPath == null) {
	    throw new ServletException("init-param '" + XSL_PATH + "' is required. Example filter config: \n"
		    + exampleFilterConfig());
	}
    }

    private String exampleFilterConfig() {
	return "<filter>\n" + "  <filter-name>Xslt</filter-name>\n" + "  <filter-class>" + this.getClass().getName()
		+ "</filter-class>\n" + "  <init-param>\n" + "    <param-name>" + XSL_PATH + "</param-name>\n"
		+ "    <param-value>/xml/html.xsl</param-value>\n" + "  </init-param>\n" + "</filter>";
    }

    @Override
    public void destroy() {
	this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	    ServletException {
	long startFilter = System.currentTimeMillis();

	log.warning("Applying XsltFilter on " + ServletToStringUtil.toString(request));
	String contentType;
	String styleSheet;
	contentType = "text/html";
	styleSheet = this.xslPath;
	response.setContentType(contentType);

	PrintWriter out = response.getWriter();
	CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
	try {
	    URL stylePathURL = this.getClass().getResource(styleSheet);
	    String stylePath = stylePathURL.toString();
	    Source styleSource = new StreamSource(stylePath);

	    long startChain = System.currentTimeMillis();
	    chain.doFilter(request, responseWrapper);
	    long endChain = System.currentTimeMillis();

	    // Get response from servlet
	    StringReader sr = new StringReader(new String(responseWrapper.toString()));
	    Source xmlSource = new StreamSource(sr);

	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer(styleSource);
	    CharArrayWriter caw = new CharArrayWriter();
	    StreamResult result = new StreamResult(caw);
	    transformer.transform(xmlSource, result);
	    // response.setContentLength(caw.toString().length());
	    out.write(caw.toString());

	    long endFilter = System.currentTimeMillis();
	    long totalTime = endFilter - startFilter;
	    long totalChainCall = endChain - startChain;
	    long totalTransformationTime = totalTime - totalChainCall;

	    log.warning("Finishing Applying XsltFilter. [TT:" + totalTime + ",CT:" + totalChainCall + ",Tr:"
		    + totalTransformationTime + "] on " + ServletToStringUtil.toString(request));
	} catch (Exception ex) {
	    log.log(Level.WARNING, "Error while transforming. Returning unchanged content.", ex);
	    out.write(responseWrapper.toString());
	}
    }

    public FilterConfig getFilterConfig() {
	return filterConfig;
    }
}