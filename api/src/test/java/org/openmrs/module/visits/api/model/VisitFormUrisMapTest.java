package org.openmrs.module.visits.api.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.visits.BaseTest;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.module.visits.api.util.VisitsConstants.CREATE_URI_NAME;
import static org.openmrs.module.visits.api.util.VisitsConstants.EDIT_URI_NAME;

public class VisitFormUrisMapTest extends BaseTest {
    public static final String VISIT_TYPE_1_NAME = "Follow-up";
    public static final String VISIT_TYPE_1_UUID = UUID.randomUUID().toString();

    public static final String INVALID_URI = "&&  @@@";

    public static final String VISIT_TYPE_1_CREATE_URI = "/a";
    public static final String VISIT_TYPE_1_EDIT_URI = "/b";
    public static final String DEFAULT_VISIT_TYPE_CREATE_URI = "/d";
    public static final String DEFAULT_VISIT_TYPE_EDIT_URI = "/e";

    public static final String EMPTY_STRING = "";
    public static final String INVALID_JSON = "{";

    @Test
    public void shouldParseTypeWith2Uris() {
        String input = getInputForType1With2Uri();
        new VisitFormUrisMap(input);
    }

    @Test
    public void shouldParseTypeWith1Uris() {
        String input = getInputForType1With1Uri();
        new VisitFormUrisMap(input);
    }

    @Test
    public void shouldParseJsonWithDefaultUri() {
        String input = getInputForDefaultType2With2Uris();
        new VisitFormUrisMap(input);
    }

    @Test
    public void shouldParseJsonWithNoUris() {
        String input = getInputWithoutUris();
        new VisitFormUrisMap(input);
    }

    @Test
    public void shouldParseEmptyInput() {
        new VisitFormUrisMap(EMPTY_STRING);
    }

    @Test
    public void shouldNoThrowExceptionIfJsonIsInvalid() {
        new VisitFormUrisMap(INVALID_JSON);
    }

    @Test
    public void shouldReturnNullUriIfEmptyMap() {
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(EMPTY_STRING);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID,  true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID,  false));

        Assert.isNull(actualForCreation);
        Assert.isNull(actualForEdit);
    }

    @Test
    public void shouldReturnNullUriIfUrlIsInvalidEvenThoughThereAreDefaultUris() {
        String input = getInputForType1WithInvalidUrisAndForValidDefaultUris();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        Assert.isNull(actualForCreation);
        Assert.isNull(actualForEdit);
    }

    @Test
    public void shouldReturnDefaultUriWhenSpecifiedUrlsAreNotSet() {
        String input = getInputForDefaultType2With2Uris();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(DEFAULT_VISIT_TYPE_CREATE_URI, actualForCreation);
        assertEquals(DEFAULT_VISIT_TYPE_EDIT_URI, actualForEdit);
    }

    @Test
    public void shouldReturnSpecifiedUriByTypeName() {
        String input = getInputForType1With2Uri();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(VISIT_TYPE_1_CREATE_URI, actualForCreation);
        assertEquals(VISIT_TYPE_1_EDIT_URI, actualForEdit);
    }

    @Test
    public void shouldReturnSpecifiedUriBasedOnTypeUuid() {
        String input = getInputForType1With2UriBasedOnTypeUuid();
        VisitFormUrisMap visitFormUrisMap = new VisitFormUrisMap(input);

        String actualForCreation = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, true));
        String actualForEdit = visitFormUrisMap.getUri(createVisit(VISIT_TYPE_1_NAME, VISIT_TYPE_1_UUID, false));

        assertEquals(VISIT_TYPE_1_CREATE_URI, actualForCreation);
        assertEquals(VISIT_TYPE_1_EDIT_URI, actualForEdit);
    }

    private String getInputForType1With2Uri() {
        return String.format("{'%s':{"
                            + "'%s':'%s',"
                            + "'%s':'%s'}}",
                VISIT_TYPE_1_NAME,
                CREATE_URI_NAME, VISIT_TYPE_1_CREATE_URI,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForType1With2UriBasedOnTypeUuid() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}}",
                VISIT_TYPE_1_UUID,
                CREATE_URI_NAME, VISIT_TYPE_1_CREATE_URI,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForType1With1Uri() {
        return String.format("{'%s':{"
                        + "'%s':'%s'}}",
                VISIT_TYPE_1_NAME,
                EDIT_URI_NAME, VISIT_TYPE_1_EDIT_URI);
    }

    private String getInputForDefaultType2With2Uris() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}}",
                VisitFormUrisMap.DEFAULT_VISIT_FORM_URIS_KEY,
                CREATE_URI_NAME, DEFAULT_VISIT_TYPE_CREATE_URI,
                EDIT_URI_NAME, DEFAULT_VISIT_TYPE_EDIT_URI);
    }

    private String getInputForType1WithInvalidUrisAndForValidDefaultUris() {
        return String.format("{'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'},"
                        + "'%s':{"
                        + "'%s':'%s',"
                        + "'%s':'%s'}"
                        + "}",
                VisitFormUrisMap.DEFAULT_VISIT_FORM_URIS_KEY,
                CREATE_URI_NAME, DEFAULT_VISIT_TYPE_CREATE_URI,
                EDIT_URI_NAME, DEFAULT_VISIT_TYPE_EDIT_URI,
                VISIT_TYPE_1_NAME,
                CREATE_URI_NAME, INVALID_URI,
                EDIT_URI_NAME, INVALID_URI);
    }

    private String getInputWithoutUris() {
        return "{}";
    }

    private Visit createVisit(String typeName, String typeUuid, boolean isFormCreation) {
        List<Encounter> encountersList = Mockito.mock(List.class);
        VisitType visitType = Mockito.mock(VisitType.class);
        Visit visit = Mockito.mock(Visit.class);
        Patient patient = Mockito.mock(Patient.class);

        when(visit.getVisitType()).thenReturn(visitType);
        when(visit.getPatient()).thenReturn(patient);
        when(visit.getNonVoidedEncounters()).thenReturn(encountersList);

        when(visitType.getName()).thenReturn(typeName);
        when(visitType.getUuid()).thenReturn(typeUuid);
        when(patient.getUuid()).thenReturn(UUID.randomUUID().toString());
        when(encountersList.isEmpty()).thenReturn(isFormCreation);

        return visit;
    }
}
