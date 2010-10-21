package com.google.code.unlp.tesis.volatiler.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class VolatilerRegistryListener implements ServletContextListener {

    private static final String VOLATILE_REGISTRY_KEY = "VOLATILE_REGISTRY_KEY";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	sce.getServletContext().setAttribute(VOLATILE_REGISTRY_KEY, new VolatileRegistry());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	sce.getServletContext().setAttribute(VOLATILE_REGISTRY_KEY, null);
    }

    public static VolatileRegistry getVolatileRegistry(ServletContext servletContext) {
	return (VolatileRegistry) servletContext.getAttribute(VOLATILE_REGISTRY_KEY);
    }

}
