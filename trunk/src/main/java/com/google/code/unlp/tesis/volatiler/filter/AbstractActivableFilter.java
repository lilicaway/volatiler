/**
 * 
 */
package com.google.code.unlp.tesis.volatiler.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.code.unlp.tesis.volatiler.servlet.VolatileRegistry;
import com.google.code.unlp.tesis.volatiler.servlet.VolatilerRegistryListener;

/**
 * 
 */
public abstract class AbstractActivableFilter implements Filter {

    public static final String FILTER_ACTIVE_BY_DEFAULT = "activeByDefault";
    private FilterConfig filterConfig;
    private VolatileRegistry registry;
    protected final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * @see Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        registry = VolatilerRegistryListener.getVolatileRegistry(filterConfig
                .getServletContext());

        String activeByDefault = filterConfig
                .getInitParameter(FILTER_ACTIVE_BY_DEFAULT);
        registry.register(this,
                activeByDefault == null || Boolean.valueOf(activeByDefault));

        doInit();
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public final void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (isActive(request, response)) {
            doDoFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public final void destroy() {
        doDestroy();
    }

    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public String getName() {
        return filterConfig.getFilterName();
    }

    /**
     * This is called by {@link #init(FilterConfig)} after having stored the
     * 
     * @throws ServletException
     */
    protected abstract void doInit() throws ServletException;

    protected abstract void doDestroy();

    protected boolean isActive(ServletRequest request, ServletResponse response) {
        return registry.isActive(this);
    }

    protected abstract void doDoFilter(ServletRequest request,
            ServletResponse response, FilterChain chain) throws IOException,
            ServletException;
}
