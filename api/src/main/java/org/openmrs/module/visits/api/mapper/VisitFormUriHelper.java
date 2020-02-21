package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;

import org.apache.commons.validator.routines.UrlValidator;
import org.openmrs.module.visits.api.exception.ValidationException;

import static org.openmrs.module.visits.api.util.ConfigConstants.PATIENT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.GlobalPropertiesConstants.VISIT_FORM_URI;

public final class VisitFormUriHelper {

    private static final String PREFIX_URL = "https://demo.openmrs.org/openmrs";

    private static final String START_PARENTHESIS = "\\{\\{";

    private static final String START_PARENTHESIS_PLAIN = "{{";

    private static final String END_PARENTHESIS = "\\}\\}";

    private static final String END_PARENTHESIS_PLAIN = "}}";

    private static final UrlValidator VALIDATOR = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);

    public static String getVisitFormUri(Patient patient, Visit visit) throws ValidationException {
        String pattern = getVisitFormUriPattern();
        String patternWithPatient = applyId(pattern, PATIENT_UUID_PARAM, patient.getUuid());
        String stringUri = applyId(patternWithPatient, VISIT_UUID_PARAM, visit.getUuid());
        if (isInvalid(stringUri, pattern)) {
            throw new ValidationException("Visit Form URI is invalid. Please check " + VISIT_FORM_URI.getKey() +
                    " global property.");
        }
        return stringUri;
    }

    private static boolean isInvalid(String stringUri, String pattern) {
        return !isPathValid(stringUri) || !isPatternValid(pattern);
    }

    private static boolean isPatternValid(String pattern) {
        return isPathValid(pattern) &&
                StringUtils.contains(pattern,
                        START_PARENTHESIS_PLAIN + PATIENT_UUID_PARAM + END_PARENTHESIS_PLAIN) &&
                StringUtils.contains(pattern,
                        START_PARENTHESIS_PLAIN + VISIT_UUID_PARAM + END_PARENTHESIS_PLAIN);
    }

    private static boolean isPathValid(String path) {
        return VALIDATOR.isValid(PREFIX_URL + path);
    }

    private static String applyId(String pattern, String key, String id) {
        return pattern.replaceAll(START_PARENTHESIS + key + END_PARENTHESIS, id);
    }

    private static String getVisitFormUriPattern() {
        return Context.getAdministrationService().getGlobalProperty(VISIT_FORM_URI.getKey());
    }

    private VisitFormUriHelper() {
    }
}
