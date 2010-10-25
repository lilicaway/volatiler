/**
 * 
 */
package com.google.code.unlp.tesis.volatiler.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.google.code.unlp.tesis.volatiler.affinity.AffinityResolver;
import com.google.code.unlp.tesis.volatiler.affinity.BaseAffinityResolver;
import com.google.code.unlp.tesis.volatiler.affinity.Initializable;

/**
 * This abstract class just leaves the {@link FilterConfig} and the
 * {@link AffinityResolver} available for subclasses to use by the methods
 * {@link #getFilterConfig()} and {@link #getAffinityResolver()}, respectively.
 */
public abstract class AbstractAffinityActivableFilter extends AbstractActivableFilter {

    public static final String AFFINITY_RESOLVER_PARAMETER = "affinityResolver";

    private volatile AffinityResolver affinityResolver;

    @Override
    public void doInit() throws ServletException {
	String affinityResolverClassName = getFilterConfig().getInitParameter(AFFINITY_RESOLVER_PARAMETER);
	if (affinityResolverClassName == null) {
	    affinityResolverClassName = BaseAffinityResolver.class.getName();
	}
	try {
	    Class<?> affinityResolverClass = Class.forName(affinityResolverClassName);
	    affinityResolver = (AffinityResolver) affinityResolverClass.newInstance();
	    if (affinityResolver instanceof Initializable) {
		((Initializable) affinityResolver).init(getFilterConfig());
	    }
	} catch (ClassNotFoundException e) {
	    throw new ServletException("Could not find class for " + AFFINITY_RESOLVER_PARAMETER + "='"
		    + affinityResolverClassName + "': " + e.getMessage(), e);
	} catch (InstantiationException e) {
	    throw new ServletException("Could not instantiate class for " + AFFINITY_RESOLVER_PARAMETER + "='"
		    + affinityResolverClassName + "': " + e.getMessage(), e);
	} catch (IllegalAccessException e) {
	    throw new ServletException("Could not find public no-argument constructor for "
		    + AFFINITY_RESOLVER_PARAMETER + "='" + affinityResolverClassName + "': " + e.getMessage(), e);
	} catch (ClassCastException e) {
	    throw new ServletException("The " + AFFINITY_RESOLVER_PARAMETER + "='" + affinityResolverClassName
		    + "' must be of type" + AffinityResolver.class.getName() + ": " + e.getMessage(), e);
	}
    }


    @Override
    public void doDestroy() {
	affinityResolver = null;
    }

    protected AffinityResolver getAffinityResolver() {
	return affinityResolver;
    }
}
