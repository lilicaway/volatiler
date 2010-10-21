/**
 * 
 */
package com.google.code.unlp.tesis.volatiler.affinity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This base implementation just return true in both methods and it is intended
 * to be used if you just need to implement only one of {@link AffinityResolver
 * AffinityResolver's} methods.
 */
public class BaseAffinityResolver implements AffinityResolver {

    /**
     * @return true
     */
    @Override
    public boolean matchAffinityBeforeFilterChain(ServletRequest request, ServletResponse response) {
	return true;
    }

    /**
     * @return true
     */
    @Override
    public boolean matchAffinityAfterFilterChain(ServletRequest request, ServletResponse response) {
	return true;
    }

}
