/**
 * 
 */
package com.google.code.unlp.tesis.volatiler.affinity;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.google.code.unlp.tesis.volatiler.filter.AbstractActivableFilter;

/**
 * If this interface is implemented by an {@link AffinityResolver} its
 * {@link #init(FilterConfig)} method will be called when the associated
 * {@link AbstractActivableFilter} is initialized by Servlet Container.
 */
public interface Initializable {

    /**
     * 
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException;
}
