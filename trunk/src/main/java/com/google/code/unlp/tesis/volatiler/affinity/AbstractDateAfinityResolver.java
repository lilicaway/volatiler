package com.google.code.unlp.tesis.volatiler.affinity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class AbstractDateAfinityResolver extends BaseAffinityResolver {

    @Override
    public boolean matchAffinityAfterFilterChain(ServletRequest request, ServletResponse response) {
	return isActiveNow(request, response);
    }

    @Override
    public boolean matchAffinityBeforeFilterChain(ServletRequest request, ServletResponse response) {
	return isActiveNow(request, response);
    }

    public abstract boolean isActiveNow(ServletRequest request, ServletResponse response);
}
