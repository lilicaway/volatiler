package com.google.code.unlp.tesis.volatiler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class AddSomethingAtTheBottomFilter implements Filter {
    private FilterConfig filterConfig = null;
    private static final Logger log = Logger.getLogger(AddSomethingAtTheBottomFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
	this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
	    ServletException {
	log.warning("Adding something at the bottom of " + ServletToStringUtil.toString(request));

	filterChain.doFilter(request, response);
	response.getWriter().append("<h1>Filter is HERE!</h1>");
	log.warning("Finishing Adding something at the bottom of " + ServletToStringUtil.toString(request));
    }

    public FilterConfig getFilterConfig() {
	return filterConfig;
    }
}