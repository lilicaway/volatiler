package com.google.code.unlp.tesis.volatiler.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VolatilityActivatorConsoleServlet extends HttpServlet {

    private static final String FILTER_NAME_PARAMETER = "filterName";
    private static final String ACTION_PARAMETER = "action";
    private static final String ACTIVATE_ACTION = "Activate";
    private static final String DEACTIVATE_ACTION = "Deactivate";
    private VolatileRegistry registry;

    @Override
    public void init() throws ServletException {
	registry = VolatilerRegistryListener.getVolatileRegistry(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	PrintWriter out = resp.getWriter();
	out.append("<html>\n<head>\n<title>Volatile Activator Console</title>\n</head>");
	out.append("<body>\n");
	out.append("<p><a href='").append(req.getRequestURI()).append("'>Refresh</a></p>");

	Set<Entry<String, Boolean>> allFiltersWithActivationState = registry.getAllFiltersWithActivationState();

	renderTableWithCurrentStatus(req, out, allFiltersWithActivationState);

	out.append("</body>\n</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	String filterName = req.getParameter(FILTER_NAME_PARAMETER);
	String action = req.getParameter(ACTION_PARAMETER);
	if (registry.contains(filterName)) {
	    boolean activate = ACTIVATE_ACTION.equals(action);
	    registry.setActivate(filterName, activate);
	}
	doGet(req, resp);
    }

    private void renderTableWithCurrentStatus(HttpServletRequest req, PrintWriter out,
	    Set<Entry<String, Boolean>> allFiltersWithActivationState) {

	out.append("<table border='1'>\n<tr><th>Volatile Service</th><th>State</th><th>Change State</th></tr>\n");
	for (Entry<String, Boolean> filterActiveationState : allFiltersWithActivationState) {
	    String filterName = filterActiveationState.getKey();
	    boolean isActive = Boolean.TRUE.equals(filterActiveationState.getValue());

	    out.append("<tr><td>").append(filterName).append("</td><td>");
	    out.append(isActive ? "Active" : "Inactive").append("</td><td>");
	    out.append("<form method='POST' action='").append(req.getRequestURI());
	    out.append("'><input type='hidden' name='").append(FILTER_NAME_PARAMETER);
	    out.append("' value='").append(filterName);
	    out.append("'/><input type='submit' name='").append(ACTION_PARAMETER);
	    out.append("' value='").append(isActive ? DEACTIVATE_ACTION : ACTIVATE_ACTION);
	    out.append("' /></form></td></tr>\n");
	}
	out.append("</table>");
    }

}
