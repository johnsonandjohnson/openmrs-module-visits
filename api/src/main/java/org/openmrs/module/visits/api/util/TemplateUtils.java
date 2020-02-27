package org.openmrs.module.visits.api.util;

import java.util.Map;

public final class TemplateUtils {
    private static final String START_PARENTHESIS_REGEX = "\\{\\{";
    private static final String END_PARENTHESIS_REGEX = "\\}\\}";

    public static String fillTemplate(String template, Map<String, String> params) {
        String result = template;
        for (Map.Entry<String, String> param : params.entrySet()) {
            result = fillTemplate(result, param.getKey(), param.getValue());
        }
        return result;
    }

    public static String fillTemplate(String template, String param, String value) {
        return template.replaceAll(START_PARENTHESIS_REGEX + param + END_PARENTHESIS_REGEX, value);
    }

    private TemplateUtils() {
    }
}
