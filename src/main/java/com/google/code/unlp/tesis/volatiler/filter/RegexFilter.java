package com.google.code.unlp.tesis.volatiler.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.code.unlp.tesis.volatiler.CharResponseWrapper;
import com.google.code.unlp.tesis.volatiler.ServletToStringUtil;

public final class RegexFilter extends AbstractAffinityActivableFilter {

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String REG_EX_INIT_PARAM = "regex";

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String REPLACEMENT_INIT_PARAM = "replacement";

    private static final Logger log = Logger.getLogger(RegexFilter.class.getName());

    private volatile Pattern regex;

    private volatile String replacement;

    @Override
    public void doInit() throws ServletException {
	super.doInit();
	String regexString = getFilterConfig().getInitParameter(REG_EX_INIT_PARAM);
	if (regexString == null) {
	    throw new ServletException("init-param '" + REG_EX_INIT_PARAM + "' is required.");
	}
	regex = Pattern.compile(regexString, Pattern.MULTILINE);
	replacement = getFilterConfig().getInitParameter(REPLACEMENT_INIT_PARAM);
	if (replacement == null) {
	    throw new ServletException("init-param '" + REPLACEMENT_INIT_PARAM + "' is required.");
	}

    }

    @Override
    public void doDestroy() {
    }

    @Override
    public void doDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	    ServletException {
	long startFilter = System.currentTimeMillis();

	if (!getAffinityResolver().matchAffinityBeforeFilterChain(request, response)) {
	    log.warning("Filter " + getFilterConfig().getFilterName() + " not applied because of affinity on "
		    + ServletToStringUtil.toString(request));
	    chain.doFilter(request, response);
	    return;
	}

	log.warning("Applying Filter " + getFilterConfig().getFilterName() + " on "
		+ ServletToStringUtil.toString(request));

	response.setContentType("text/html");

	PrintWriter out = response.getWriter();
	CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
	try {
	    long startChain = System.currentTimeMillis();
	    chain.doFilter(request, responseWrapper);
	    long endChain = System.currentTimeMillis();

	    final String originalContent = responseWrapper.toString();

	    if (getAffinityResolver().matchAffinityAfterFilterChain(request, responseWrapper)) {
		Matcher matcher = regex.matcher(originalContent);
		final String changedContent = matcher.replaceAll(replacement);
		// DO NOT write the content length or we will prevent outer
		// filters from appending more content.
		// response.setContentLength(caw.toString().length());
		out.write(changedContent);
	    } else {
		out.write(originalContent);
	    }

	    long endFilter = System.currentTimeMillis();
	    long totalTime = endFilter - startFilter;
	    long totalChainCall = endChain - startChain;
	    long totalTransformationTime = totalTime - totalChainCall;

	    log.warning("Finishing Applying Filter " + getFilterConfig().getFilterName() + ". [TT:" + totalTime
		    + ",CT:" + totalChainCall + ",Tr:" + totalTransformationTime + "] on "
		    + ServletToStringUtil.toString(request));
	} catch (Exception ex) {
	    log.log(Level.WARNING, "Error while transforming. Returning unchanged content.", ex);
	    out.write(responseWrapper.toString());
	}
    }

}