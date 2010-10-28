package com.google.code.unlp.tesis.volatiler.filter.regex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.code.unlp.tesis.volatiler.CharResponseWrapper;
import com.google.code.unlp.tesis.volatiler.ServletToStringUtil;
import com.google.code.unlp.tesis.volatiler.filter.AbstractAffinityActivableFilter;

public abstract class AbstractRegexMultipleReplacementFilter extends
        AbstractAffinityActivableFilter {

    public abstract List<Replacer> getReplacers();

    @Override
    public void doDoFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        long startFilter = System.currentTimeMillis();

        if (!getAffinityResolver().matchAffinityBeforeFilterChain(request,
                response)) {
            log.warning("Filter " + getFilterConfig().getFilterName()
                    + " not applied because of affinity on "
                    + ServletToStringUtil.toString(request));
            chain.doFilter(request, response);
            return;
        }

        log.warning("Applying Filter " + getFilterConfig().getFilterName()
                + " on " + ServletToStringUtil.toString(request));

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        CharResponseWrapper responseWrapper = new CharResponseWrapper(
                (HttpServletResponse) response);
        try {
            long startChain = System.currentTimeMillis();
            chain.doFilter(request, responseWrapper);
            long endChain = System.currentTimeMillis();

            final String originalContent = responseWrapper.toString();

            if (getAffinityResolver().matchAffinityAfterFilterChain(request,
                    responseWrapper)) {
                String changedContent = originalContent;
                for (Replacer replacer : getReplacers()) {
                    Matcher matcher = replacer.getRegex().matcher(
                            changedContent);
                    changedContent = matcher.replaceAll(replacer
                            .getReplacement());
                }
                // DO NOT write the content length or we will prevent outer
                // filters from appending more content.
                // response.setContentLength(caw.toString().length());
                out.write(changedContent);
            } else {
                out.write(originalContent);
            }

            long endFilter = System.currentTimeMillis();
            long totalTime = endFilter - startFilter;
            long totalChainCall = endChain - startChain;
            long totalTransformationTime = totalTime - totalChainCall;

            log.warning("Finishing Applying Filter "
                    + getFilterConfig().getFilterName() + ". [TT:" + totalTime
                    + ",CT:" + totalChainCall + ",Tr:"
                    + totalTransformationTime + "] on "
                    + ServletToStringUtil.toString(request));
        } catch (Exception ex) {
            log.log(Level.WARNING,
                    "Error while transforming. Returning unchanged content.",
                    ex);
            out.write(responseWrapper.toString());
        }
    }

}
