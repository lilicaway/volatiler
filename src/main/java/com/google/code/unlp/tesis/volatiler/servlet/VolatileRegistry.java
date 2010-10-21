package com.google.code.unlp.tesis.volatiler.servlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.code.unlp.tesis.volatiler.filter.AbstractActivableFilter;

public class VolatileRegistry {

    private final Map<String, Boolean> volatileFilters = Collections.synchronizedMap(new HashMap<String, Boolean>());

    public boolean isActive(AbstractActivableFilter filter) {
	Boolean isActive = volatileFilters.get(filter.getName());
	return isActive != null && isActive.booleanValue();
    }

    public void register(AbstractActivableFilter filter, boolean activeByDefault) {
	register(filter.getName(), activeByDefault);
    }

    private void register(String filterName, boolean activeByDefault) {
	volatileFilters.put(filterName, Boolean.valueOf(activeByDefault));
    }

    public void setActivate(String filterName, boolean active) {
	register(filterName, active);
    }

    public Set<Entry<String, Boolean>> getAllFiltersWithActivationState() {
	return Collections.unmodifiableMap(volatileFilters).entrySet();
    }

    public boolean contains(String filterName) {
	return volatileFilters.containsKey(filterName);
    }
}
