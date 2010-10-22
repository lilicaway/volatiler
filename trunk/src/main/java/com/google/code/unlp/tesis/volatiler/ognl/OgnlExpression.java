package com.google.code.unlp.tesis.volatiler.ognl;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

public class OgnlExpression {
    private static final ThreadLocal<OgnlContext> DEFAULT_CONTEXT = new ThreadLocal<OgnlContext>() {
	@Override
	protected OgnlContext initialValue() {
	    return new OgnlContext();
	}
    };

    public static OgnlContext getDefaultContext() {
	return DEFAULT_CONTEXT.get();
    }

    private final Object parsedExpression;

    public OgnlExpression(String expressionString) throws OgnlException {
	if (expressionString == null) {
	    throw new OgnlException("expression string cannot be null");
	}
	parsedExpression = Ognl.parseExpression(expressionString);
    }

    public Object getParsedExpression() {
	return parsedExpression;
    }

    public Object getValue(Object rootObject) throws OgnlException {
	return getValue(getDefaultContext(), rootObject);
    }

    public Object getValue(OgnlContext context, Object rootObject) throws OgnlException {
	return Ognl.getValue(getParsedExpression(), context, rootObject);
    }

    public void setValue(Object rootObject, Object value) throws OgnlException {
	setValue(getDefaultContext(), rootObject, value);
    }

    public void setValue(OgnlContext context, Object rootObject, Object value) throws OgnlException {
	Ognl.setValue(getParsedExpression(), context, rootObject, value);
    }
}