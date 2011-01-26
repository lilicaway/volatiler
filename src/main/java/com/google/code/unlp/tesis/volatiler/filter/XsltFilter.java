package com.google.code.unlp.tesis.volatiler.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
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
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.code.unlp.tesis.volatiler.CharResponseWrapper;
import com.google.code.unlp.tesis.volatiler.ServletToStringUtil;
import com.google.code.unlp.tesis.volatiler.permormanceDTD.UrlDtdMap;

public final class XsltFilter extends AbstractAffinityActivableFilter {

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String XSL_PATH = "xslPath";

    private static final Logger log = Logger.getLogger(XsltFilter.class
	    .getName());

    private volatile String xslPath;

    private final ThreadLocal<Transformer> transformer = new ThreadLocal<Transformer>() {
	@Override
	protected Transformer initialValue() {
	    URL stylePathURL = this.getClass().getResource(xslPath);
	    if (stylePathURL == null) {
		throw new IllegalArgumentException(
			"Could not find resource on xslPath='" + xslPath
				+ "' on filter "
				+ getFilterConfig().getFilterName());
	    }
	    String stylePath = stylePathURL.toString();
	    Source styleSource = new StreamSource(stylePath);
	    TransformerFactory transformerFactory = TransformerFactory
		    .newInstance();
	    try {
		return transformerFactory.newTransformer(styleSource);
	    } catch (TransformerConfigurationException e) {
		throw new IllegalArgumentException(
			"Could not create xsl Transformer: " + e.getMessage(),
			e);
	    }
	}
    };

    @Override
    public void doInit() throws ServletException {
	super.doInit();
	xslPath = getFilterConfig().getInitParameter(XSL_PATH);
	if (xslPath == null) {
	    throw new ServletException("init-param '" + XSL_PATH
		    + "' is required.");
	}
	try {
	    // This call ensures that we raise an exception early, when the
	    // Filter is being initialized, if there is a problem in the xsl
	    // configuration. The idea is to fail fast if there is a problem.
	    getTransformer();
	} catch (IllegalArgumentException e) {
	    throw new ServletException(e);
	}
    }

    public Transformer getTransformer() {
	return transformer.get();
    }

    @Override
    public void doDestroy() {
    }

    @Override
    public void doDoFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {
	long startFilter = System.currentTimeMillis();

	if (!getAffinityResolver().matchAffinityBeforeFilterChain(request,
		response)) {
	    log.warning("XsltFilter (" + getFilterConfig().getFilterName()
		    + ") not applied because of affinity on "
		    + ServletToStringUtil.toString(request));
	    chain.doFilter(request, response);
	    // el browser tiene todo
	    return;
	}

	log.warning("Applying XsltFilter (" + getFilterConfig().getFilterName()
		+ ") on " + ServletToStringUtil.toString(request));

	response.setContentType("text/html");

	PrintWriter out = response.getWriter();
	CharResponseWrapper responseWrapper = new CharResponseWrapper(
		(HttpServletResponse) response);
	try {
	    long startChain = System.currentTimeMillis();
	    chain.doFilter(request, responseWrapper);
	    // el browse todavía no tiene nada. Está todo en memoria en el
	    // wrapper
	    long endChain = System.currentTimeMillis();

	    if (getAffinityResolver().matchAffinityAfterFilterChain(request,
		    responseWrapper)) {

		// Get response from servlet
		StringReader sr = new StringReader(new String(
			responseWrapper.toString()));
		InputSource xmlSource = new InputSource(sr);

		// Transform the response
		CharArrayWriter caw = new CharArrayWriter();
		StreamResult result = new StreamResult(caw);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		XMLReader r = spf.newSAXParser().getXMLReader();
		EntityResolver er = new EntityResolver() {
		    @Override
		    public InputSource resolveEntity(String publicId,
			    String systemId) throws SAXException, IOException {
			UrlDtdMap dtdUrlToResource = new UrlDtdMap();
			String resourcePath = dtdUrlToResource
				.getSystemValue(systemId);
			if (resourcePath != null) {
			    InputStream resourceAsStream = this.getClass()
				    .getResourceAsStream(resourcePath);
			    InputSource inputSource = new InputSource(
				    resourceAsStream);
			    inputSource.setPublicId(publicId);
			    inputSource.setSystemId(systemId);
			    return inputSource;
			} else {
			    return null;
			}
		    }
		};
		r.setEntityResolver(er);

		SAXSource s = new SAXSource(r, xmlSource);
		getTransformer().transform(s, result);

		// DO NOT write the content length or we will prevent outer
		// filters from appending more content.
		// response.setContentLength(caw.toString().length());
		out.write(caw.toString());
	    } else {
		out.write(responseWrapper.toString());
	    }

	    long endFilter = System.currentTimeMillis();
	    long totalTime = endFilter - startFilter;
	    long totalChainCall = endChain - startChain;
	    long totalTransformationTime = totalTime - totalChainCall;

	    log.warning("Finishing Applying XsltFilter ("
		    + getFilterConfig().getFilterName() + ")  [TT:" + totalTime
		    + ",CT:" + totalChainCall + ",Tr:"
		    + totalTransformationTime + "] on "
		    + ServletToStringUtil.toString(request));
	} catch (Exception ex) {
	    log.log(Level.WARNING, "Error while transforming("
		    + getFilterConfig().getFilterName()
		    + ") . Returning unchanged content.", ex);
	    out.write(responseWrapper.toString());
	}
    }

}