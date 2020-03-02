package org.openmrs.module.visits.api.util;

import static org.openmrs.module.visits.api.dto.VisitFormUrisMap.DEFAULT_VISIT_FORM_URIS_KEY;
import static org.openmrs.module.visits.api.util.ConfigConstants.ENCOUNTER_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.PATIENT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.VisitsConstants.CREATE_URI_NAME;
import static org.openmrs.module.visits.api.util.VisitsConstants.EDIT_URI_NAME;

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

    private static final String DEFAULT_CREATE_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{%s}}&visitId={{%s}}"
                    + "&definitionUiResource=cfl:htmlforms/cfl-visit-note.xml",
            PATIENT_UUID_PARAM, VISIT_UUID_PARAM);

    private static final String DEFAULT_EDIT_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{%s}}&encounterId={{%s}}",
            PATIENT_UUID_PARAM, ENCOUNTER_UUID_PARAM);

    public static final GPDefinition VISIT_FORM_URIS = new GPDefinition(
            "visits.visit-form-uris",
                String.format(
                            "{'%s':{"
                            + "'%s':'%s',"
                            + "'%s':'%s'}}",
                    DEFAULT_VISIT_FORM_URIS_KEY,
                    CREATE_URI_NAME, DEFAULT_CREATE_VISIT_FORM_URI,
                    EDIT_URI_NAME, DEFAULT_EDIT_VISIT_FORM_URI),
            String.format(
                    "The URI templates which leads to current visit form. The value of this property is a JSON "
                        + "map. The map allows to specify URIs based on the visit type. The key in the map could be "
                        + "visitTypeUuid or visitTypeName or 'default'. The value is a nested JSON map which could "
                        + "consists of 2 entries - 'create' and 'edit' URI templates. The template could consists of "
                        + "{{%s}} and {{%s}} variables which will be replaced if URLs are used. \n "
                        + "Note: \n A) if invalid URI is set, no form will be used\n "
                        + "B) if URI is no specified, the form from 'default' settings will be used\n "
                        + "C) if specific and default URIs are not defined, no form will be used\n",
                    PATIENT_UUID_PARAM, VISIT_UUID_PARAM),
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

    public static final GPDefinition STATUS_OF_OCCURRED_VISIT = new GPDefinition(
            "visits.statusOfOccurredVisit",
            "OCCURRED",
            "The value specifying a status that will be set if the visit will be "
                    + "determined to be marked as occurred.",
            true);

    public static final GPDefinition ENCOUNTER_DATETIME_VALIDATION = new GPDefinition(
            "visits.encounterDatetimeValidation",
            "true",
            "Used to control if encounter datetime should be validated. By default OpenMRS doesn't support adding "
                    + "encounter if its date isn't between visit start and end date. This GP can be used to turn off this "
                    + "default validation. When the value is set to false then the custom EnterHtmlFormFragment is used to "
                    + "override the encounter validation in emr api and additionally OpenMRS core encounter validator "
                    + "is overridden. Possible values: true, false",
            true);

    private GlobalPropertiesConstants() { }
}
