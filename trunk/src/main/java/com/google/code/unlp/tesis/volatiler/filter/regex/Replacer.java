package com.google.code.unlp.tesis.volatiler.filter.regex;

import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

public class Replacer {

    private final Pattern regex;

    private final String replacement;

    public Replacer(Pattern regex, String replacement) {
        validateParameters(regex, replacement);

        this.regex = regex;
        this.replacement = replacement;
    }

    public Replacer(String regex, String replacement) {
        validateParameters(regex, replacement);

        this.regex = Pattern.compile(regex, Pattern.MULTILINE);
        this.replacement = replacement;

    }

    private void validateParameters(Object regex, String replacement) {
        Validate.notNull(regex, "regex cannot be null");
        Validate.notNull(replacement, "replacement cannot be null");
    }

    public Pattern getRegex() {
        return regex;
    }

    public String getReplacement() {
        return replacement;
    }

}
