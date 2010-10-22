package com.google.code.unlp.tesis.volatiler.affinity;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import ognl.OgnlException;

import com.google.code.unlp.tesis.volatiler.ognl.OgnlExpression;

public abstract class AbstractOgnlAffinityResolver extends BaseAffinityResolver implements Initializable {
    private OgnlExpression parsedOgnlExpressionForBeforeFilterChain;
    private OgnlExpression parsedOgnlExpressionForAfterFilterChain;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	parsedOgnlExpressionForBeforeFilterChain = createOgnlExpression(filterConfig,
		getOgnlExpressionForBeforeFilterChain());
	parsedOgnlExpressionForAfterFilterChain = createOgnlExpression(filterConfig,
		getOgnlExpressionForAfterFilterChain());
    }

    private OgnlExpression createOgnlExpression(FilterConfig filterConfig, String ognlExpressionString)
	    throws ServletException {
	OgnlExpression ognlExpression;
	try {
	    ognlExpression = new OgnlExpression(ognlExpressionString);
	} catch (OgnlException e) {
	    throw new ServletException("Problem when parsing the ognl expression for filter chain in "
		    + this.getClass().getName() + " at Filter " + filterConfig.getFilterName() + ". You provided: '"
		    + ognlExpressionString + "'. Error message: " + e.getMessage(), e);
	}
	return ognlExpression;
    }

    @Override
    public boolean matchAffinityBeforeFilterChain(ServletRequest request, ServletResponse response) {

	OgnlExpression ognlExpression = parsedOgnlExpressionForBeforeFilterChain;
	String ognlEpressionString = getOgnlExpressionForBeforeFilterChain();

	return evaluateOgnl(request, response, ognlExpression, ognlEpressionString);
    }

    private boolean evaluateOgnl(ServletRequest request, ServletResponse response, OgnlExpression ognlExpression,
	    String ognlEpressionString) {
	RequestResponseWrapper rootObject = new RequestResponseWrapper(request, response);
	try {
	    Object value = ognlExpression.getValue(rootObject);
	    if (value instanceof Boolean) {
		return ((Boolean) value).booleanValue();
	    } else {
		log.log(Level.WARNING,
			"The ognl expression did not return a boolean value. The affinity will NOT be applied. Returned value: '"
				+ value + "'. Expression: '" + ognlEpressionString + "' applied on " + rootObject);
		return false;
	    }
	} catch (OgnlException e) {
	    log.log(Level.WARNING, "Problem evaluating OGNL expression. Affinity will NOT be applied. Expression: '"
		    + ognlEpressionString + "' applied on " + rootObject + ". Error Message:" + e.getMessage(), e);
	    return false;
	}
    }

    public abstract String getOgnlExpressionForBeforeFilterChain();

    @Override
    public boolean matchAffinityAfterFilterChain(ServletRequest request, ServletResponse response) {
	OgnlExpression ognlExpression = parsedOgnlExpressionForAfterFilterChain;
	String ognlEpressionString = getOgnlExpressionForAfterFilterChain();

	return evaluateOgnl(request, response, ognlExpression, ognlEpressionString);
    }

    public abstract String getOgnlExpressionForAfterFilterChain();

    OgnlExpression getParsedOgnlExpressionForBeforeFilterChain() {
	return parsedOgnlExpressionForBeforeFilterChain;
    }

    OgnlExpression getParsedOgnlExpressionForAfterFilterChain() {
	return parsedOgnlExpressionForAfterFilterChain;
    }

    /**
     *
     */
    private final static class RequestResponseWrapper {
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
}
