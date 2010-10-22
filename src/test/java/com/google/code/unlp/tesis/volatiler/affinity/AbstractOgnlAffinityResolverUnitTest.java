/**
 * 
 */
package com.google.code.unlp.tesis.volatiler.affinity;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Test;

/**
 * @author eduardo.pereda
 *
 */
public class AbstractOgnlAffinityResolverUnitTest {

    @Test
    public void testHappyPath() throws Exception {
	AbstractOgnlAffinityResolver ognlResolver = new AbstractOgnlAffinityResolver() {
	    
	    @Override
	    public String getOgnlExpressionForBeforeFilterChain() {
		return "request.getAttribute('testing') == 'yes'";
	    }
	    
	    @Override
	    public String getOgnlExpressionForAfterFilterChain() {
		return "response.bufferSize > 20";
	    }
	};
	FilterConfig filterConfig = mock(FilterConfig.class);
	when(filterConfig.getFilterName()).thenReturn("someFilterName");
	ognlResolver.init(filterConfig);
	assertNotNull(ognlResolver.getParsedOgnlExpressionForBeforeFilterChain());
	assertNotNull(ognlResolver.getParsedOgnlExpressionForAfterFilterChain());

	ServletRequest request = mock(ServletRequest.class);
	ServletResponse response = mock(ServletResponse.class);
	assertFalse(ognlResolver.matchAffinityBeforeFilterChain(request, response));
	assertFalse(ognlResolver.matchAffinityAfterFilterChain(request, response));

	when(request.getAttribute("testing")).thenReturn("yes");
	when(response.getBufferSize()).thenReturn(21);
	assertTrue(ognlResolver.matchAffinityBeforeFilterChain(request, response));
	assertTrue(ognlResolver.matchAffinityAfterFilterChain(request, response));

	when(response.getBufferSize()).thenReturn(19);
	assertFalse(ognlResolver.matchAffinityAfterFilterChain(request, response));
    }

    @Test(expected = ServletException.class)
    public void testNullOgnl_After() throws Exception {
	AbstractOgnlAffinityResolver ognlResolver = new AbstractOgnlAffinityResolver() {

	    @Override
	    public String getOgnlExpressionForBeforeFilterChain() {
		return "something";
	    }

	    @Override
	    public String getOgnlExpressionForAfterFilterChain() {
		return null;
	    }
	};
	FilterConfig filterConfig = mock(FilterConfig.class);
	when(filterConfig.getFilterName()).thenReturn("someFilterName");
	ognlResolver.init(filterConfig);
    }

    @Test(expected = ServletException.class)
    public void testNullOgnl_Before() throws Exception {
	AbstractOgnlAffinityResolver ognlResolver = new AbstractOgnlAffinityResolver() {

	    @Override
	    public String getOgnlExpressionForBeforeFilterChain() {
		return null;
	    }

	    @Override
	    public String getOgnlExpressionForAfterFilterChain() {
		return "something";
	    }
	};
	FilterConfig filterConfig = mock(FilterConfig.class);
	when(filterConfig.getFilterName()).thenReturn("someFilterName");
	ognlResolver.init(filterConfig);
    }

    @Test
    public void testValidSyntaxButInvalidSemantic() throws Exception {
	AbstractOgnlAffinityResolver ognlResolver = new AbstractOgnlAffinityResolver() {

	    @Override
	    public String getOgnlExpressionForBeforeFilterChain() {
		return "non.existent";
	    }

	    @Override
	    public String getOgnlExpressionForAfterFilterChain() {
		return "response.bufferSize"; // not a boolean
	    }
	};
	FilterConfig filterConfig = mock(FilterConfig.class);
	when(filterConfig.getFilterName()).thenReturn("someFilterName");
	ognlResolver.init(filterConfig);

	ServletRequest request = mock(ServletRequest.class);
	ServletResponse response = mock(ServletResponse.class);
	assertFalse(ognlResolver.matchAffinityBeforeFilterChain(request, response));
	assertFalse(ognlResolver.matchAffinityAfterFilterChain(request, response));
    }
}
