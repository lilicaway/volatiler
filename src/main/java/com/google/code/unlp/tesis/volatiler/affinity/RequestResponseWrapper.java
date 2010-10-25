package com.google.code.unlp.tesis.volatiler.affinity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 */
public class RequestResponseWrapper {
    private final ServletRequest request;
    private final ServletResponse response;

    public RequestResponseWrapper(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public ServletRequest getRequest() {
        return request;
    }

    public ServletResponse getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "request:" + request + "; response:" + response;
    }
}