package com.google.code.unlp.tesis.volatiler.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.code.unlp.tesis.volatiler.affinity.BaseAffinityResolver;
import com.google.code.unlp.tesis.volatiler.filter.regex.AbstractRegexMultipleReplacementFilter;
import com.google.code.unlp.tesis.volatiler.filter.regex.RegexFilter;
import com.google.code.unlp.tesis.volatiler.servlet.VolatileRegistry;
import com.google.code.unlp.tesis.volatiler.servlet.VolatilerRegistryListener;

public class RegexFilterUnitTest {

    private ServletContext servletContext;

    @Before
    public void setup() {
	servletContext = mock(ServletContext.class);
	VolatileRegistry volatileRegistry = new VolatileRegistry();
	when(servletContext.getAttribute(VolatilerRegistryListener.VOLATILE_REGISTRY_KEY)).thenReturn(volatileRegistry);

    }

    @Test
    public void testReplacement() throws Exception {
	AbstractRegexMultipleReplacementFilter filter = new RegexFilter();
	FilterConfig filterConfig = mock(FilterConfig.class);
	when(filterConfig.getFilterName()).thenReturn("some filter");
	when(filterConfig.getServletContext()).thenReturn(servletContext);
	when(filterConfig.getInitParameter(AbstractRegexMultipleReplacementFilter.AFFINITY_RESOLVER_PARAMETER)).thenReturn(
		BaseAffinityResolver.class.getName());
	when(filterConfig.getInitParameter(RegexFilter.REG_EX_INIT_PARAM)).thenReturn("p>");
	when(filterConfig.getInitParameter(RegexFilter.REPLACEMENT_INIT_PARAM)).thenReturn("h4>");
	filter.init(filterConfig);

	HttpServletRequest request = mock(HttpServletRequest.class);
	when(request.getRequestURL()).thenReturn(new StringBuffer("/get/some/servlet"));
	HttpServletResponse response = mock(HttpServletResponse.class);
	CharArrayWriter charArrayWriter = new CharArrayWriter();
	when(response.getWriter()).thenReturn(new PrintWriter(charArrayWriter));

	FilterChain chain = mock(FilterChain.class);

	final String originalHtml = "<html>\n"
		+ "<head> \n"
		+ "<title>Volatile Activator Console</title>\n"
		+ "</head><body>\n"
		+ "<p><a href='/admin/volatilityActivatorConsole'>Refresh</a></p><table border='1'>\n"
		+ "<tr><th>Volatile Service</th><th>State</th><th>Change State</th></tr> \n"
		+ "<tr><td>FarmaciasMapEnhancer</td><td>Active</td><td><form method='POST' action='/admin/volatilityActivatorConsole'><input type='hidden' name='filterName' value='FarmaciasMapEnhancer'/><input type='submit' name='action' value='Deactivate' /></form></td></tr>\n"
		+ "<tr><td>AddSomethingAtTheBottom</td><td>Active</td><td><form method='POST' action='/admin/volatilityActivatorConsole'><input type='hidden' name='filterName' value='AddSomethingAtTheBottom'/><input type='submit' name='action' value='Deactivate' /></form></td></tr>\n"
		+ "</table></body> \n" + "</html>\n";

	final String expectedHtml = "<html>\n"
		+ "<head> \n"
		+ "<title>Volatile Activator Console</title>\n"
		+ "</head><body>\n"
		+ "<h4><a href='/admin/volatilityActivatorConsole'>Refresh</a></h4><table border='1'>\n"
		+ "<tr><th>Volatile Service</th><th>State</th><th>Change State</th></tr> \n"
		+ "<tr><td>FarmaciasMapEnhancer</td><td>Active</td><td><form method='POST' action='/admin/volatilityActivatorConsole'><input type='hidden' name='filterName' value='FarmaciasMapEnhancer'/><input type='submit' name='action' value='Deactivate' /></form></td></tr>\n"
		+ "<tr><td>AddSomethingAtTheBottom</td><td>Active</td><td><form method='POST' action='/admin/volatilityActivatorConsole'><input type='hidden' name='filterName' value='AddSomethingAtTheBottom'/><input type='submit' name='action' value='Deactivate' /></form></td></tr>\n"
		+ "</table></body> \n" + "</html>\n";

	doAnswer(new Answer<Object>() {
	    @Override
	    public Object answer(InvocationOnMock invocation) throws Throwable {
		HttpServletResponse response = (HttpServletResponse) invocation.getArguments()[1];
		response.getWriter().write(originalHtml);
		return null;
	    }
	}).when(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

	filter.doFilter(request, response, chain);

	assertEquals(expectedHtml, charArrayWriter.toString());
    }
}
