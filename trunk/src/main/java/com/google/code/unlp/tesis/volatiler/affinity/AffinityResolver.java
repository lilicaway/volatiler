package com.google.code.unlp.tesis.volatiler.affinity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface AffinityResolver {

    boolean matchAffinityBeforeFilterChain(ServletRequest request, ServletResponse response);

    boolean matchAffinityAfterFilterChain(ServletRequest request, ServletResponse response);

}
