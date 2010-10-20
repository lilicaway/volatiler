package com.google.code.unlp.tesis.volatiler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class HitCounterFilter implements Filter {
    private FilterConfig filterConfig = null;
    private static final Logger log = Logger.getLogger(HitCounterFilter.class.getName());

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

	long start = System.currentTimeMillis();
	filterChain.doFilter(request, response);
	long end = System.currentTimeMillis();
	log.warning("The requests took " + (end - start) + "ms: " + request.toString().replaceAll("\r?\n", "|"));
    }

    public FilterConfig getFilterConfig() {
	return filterConfig;
    }
}