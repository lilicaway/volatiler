package com.google.code.unlp.tesis.volatiler.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.code.unlp.tesis.volatiler.CharResponseWrapper;
import com.google.code.unlp.tesis.volatiler.ServletToStringUtil;

public final class XsltFilter extends AbstractAffinityActivableFilter {

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String XSL_PATH = "xslPath";

    private static final Logger log = Logger.getLogger(XsltFilter.class.getName());

    private String xslPath;

    @Override
    public void doInit() throws ServletException {
	super.doInit();
	xslPath = getFilterConfig().getInitParameter(XSL_PATH);
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
    public void doDestroy() {
    }

    @Override
    public void doDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	    ServletException {
	long startFilter = System.currentTimeMillis();

	if (!getAffinityResolver().matchAffinityBeforeFilterChain(request, response)) {
	    log.warning("XsltFilter not applied because of affinity on " + ServletToStringUtil.toString(request));
	    chain.doFilter(request, response);
	    return;
	}

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
	    if (getAffinityResolver().matchAffinityAfterFilterChain(request, responseWrapper)) {

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
	    } else {
		out.write(responseWrapper.toString());
	    }

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

}