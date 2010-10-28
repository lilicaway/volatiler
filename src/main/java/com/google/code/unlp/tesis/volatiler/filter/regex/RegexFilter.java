package com.google.code.unlp.tesis.volatiler.filter.regex;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

public final class RegexFilter extends AbstractRegexMultipleReplacementFilter {

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String REG_EX_INIT_PARAM = "regex";

    /**
     * Parameter to be used in the web.xml as an init-param.
     */
    public static final String REPLACEMENT_INIT_PARAM = "replacement";

    private volatile Replacer replacer;

    @Override
    public void doInit() throws ServletException {
        super.doInit();
        String regexString = getFilterConfig().getInitParameter(
                REG_EX_INIT_PARAM);
        if (regexString == null) {
            throw new ServletException("init-param '" + REG_EX_INIT_PARAM
                    + "' is required.");
        }
        String replacement = getFilterConfig().getInitParameter(
                REPLACEMENT_INIT_PARAM);
        if (replacement == null) {
            throw new ServletException("init-param '" + REPLACEMENT_INIT_PARAM
                    + "' is required.");
        }
        replacer = new Replacer(regexString, replacement);

    }

    @Override
    public List<Replacer> getReplacers() {
        return Arrays.asList(replacer);
    }

}