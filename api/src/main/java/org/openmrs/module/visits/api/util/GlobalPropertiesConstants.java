package org.openmrs.module.visits.api.util;

public final class GlobalPropertiesConstants {

    public static final GPDefinition PAST_VISITS_LIMIT = new GPDefinition(
            "visits.past-visits-limit",
            "1",
            "Limit of past visits displayed on the patient dashboard visits widget.",
            true);

    public static final GPDefinition UPCOMING_VISITS_LIMIT = new GPDefinition(
            "visits.upcoming-visits-limit",
            "2",
            "Limit of upcoming visits displayed on the patient dashboard visits widget.",
            true);

    public static final GPDefinition VISIT_TIMES = new GPDefinition(
            "visits.visit-times",
            "Morning,Afternoon,Evening",
            "Coma separated list of visit types used to schedule a visit.",
            true);

    public static final GPDefinition VISIT_STATUSES = new GPDefinition(
            "visits.visit-statuses",
            "SCHEDULED,OCCURRED,MISSED",
            "Coma separated list of visit statuses used to schedule a visit. " +
                    "IMPORTANT: Status of newly created visits is always set to the first element of the list.",
            true);

    public static final GPDefinition VISIT_FORM_URI = new GPDefinition(
            "visits.visit-form-uri",
            "/htmlformentryui/htmlform/" +
                    "enterHtmlFormWithStandardUi.page?" + ConfigConstants.PATIENT_UUID_PARAM +
                    "={{" + ConfigConstants.PATIENT_UUID_PARAM + "}}" +
                    "&" + ConfigConstants.VISIT_UUID_PARAM + "={{" + ConfigConstants.VISIT_UUID_PARAM + "}}" +
                    "&definitionUiResource=referenceapplication:htmlforms/simpleVisitNote.xml",
            "The URI which leads to current visit form," +
                    " which consists of patient and visit uuid.",
            true);

    public static final GPDefinition MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED = new GPDefinition(
            "visits.minimumVisitDelayToAutomaticallyMarkItAsMissed",
            "1",
            "The number of days after which "
                    + "missed visits should be automatically marked as missed.",
    true);

    public static final GPDefinition STATUSES_ENDING_VISIT = new GPDefinition(
            "visits.statusesEndingVisit",
            "OCCURRED",
            "The comma-separated list allowing to specify "
                    + "statuses which point on already completed visits. "
                    + "Visits with these statuses should not be changed to "
                    + "missed.",
    true);

    public static final GPDefinition STATUS_OF_MISSED_VISIT = new GPDefinition(
            "visits.statusOfMissedVisit",
            "MISSED",
            "The value specifying a status that will be set if the visit will be "
                    + "determined to be marked as missing.",
    true);

    private GlobalPropertiesConstants() { }
}
