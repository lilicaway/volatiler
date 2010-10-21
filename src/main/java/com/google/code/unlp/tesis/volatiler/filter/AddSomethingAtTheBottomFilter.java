package com.google.code.unlp.tesis.volatiler.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.code.unlp.tesis.volatiler.ServletToStringUtil;

public final class AddSomethingAtTheBottomFilter extends AbstractActivableFilter {
    private static final Logger log = Logger.getLogger(AddSomethingAtTheBottomFilter.class.getName());

    @Override
    public void doInit() throws ServletException {
    }

    @Override
    public void doDestroy() {
    }

    @Override
    public void doDoFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
	    throws IOException,
	    ServletException {
	log.warning("Adding something at the bottom of " + ServletToStringUtil.toString(request));

	filterChain.doFilter(request, response);
	response.getWriter().append("<h1>Filter is HERE!</h1>");
	log.warning("Finishing Adding something at the bottom of " + ServletToStringUtil.toString(request));
    }

}