package org.openmrs.module.visits.api.mapper;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_FORM_URI_DEFAULT_VALUE;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.visits.api.util.ConfigConstants.PATIENT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_UUID_PARAM;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class VisitFormUriHelperTest {

    @Before
    public void setUp() {
        mockStatic(Context.class);
        given(Context.getAdministrationService()).willReturn(mock(AdministrationService.class));
    }

    @Test
    public void shouldReturnValidURI() {
        given(Context.getAdministrationService().getGlobalProperty(Matchers.any()))
                .willReturn(VISIT_FORM_URI_DEFAULT_VALUE);
        Patient patient = new Patient();
        Visit visit = new Visit();
        visit.setPatient(patient);
        String expected = getExpectedUri(patient.getUuid(), visit.getUuid());

        String actual = VisitFormUriHelper.getVisitFormUri(patient, visit);

        assertNotNull(actual);
        assertThat(actual, equalTo(expected));
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowValidationExceptionWhenVariablesAreMissing() {
        given(Context.getAdministrationService().getGlobalProperty(Matchers.any()))
                .willReturn(getVisitFormUriMissingParenthesisValue());
        Patient patient = new Patient();
        Visit visit = new Visit();
        visit.setPatient(patient);

        VisitFormUriHelper.getVisitFormUri(patient, visit);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowValidationExceptionWhenUriIsNotValid() {
        given(Context.getAdministrationService().getGlobalProperty(Matchers.any()))
                .willReturn(getVisitFormUriInvalidValue());
        Patient patient = new Patient();
        Visit visit = new Visit();
        visit.setPatient(patient);

        VisitFormUriHelper.getVisitFormUri(patient, visit);
    }

    private static String getExpectedUri(String patientUuid, String visitUuid) {
        return "/htmlformentryui/htmlform/" +
                "enterHtmlFormWithStandardUi.page?" + PATIENT_UUID_PARAM + "=" + patientUuid +
                "&" + VISIT_UUID_PARAM + "=" + visitUuid +
                "&definitionUiResource=referenceapplication:htmlforms/simpleVisitNote.xml";
    }

    private static String getVisitFormUriMissingParenthesisValue() {
        return "/htmlformentryui/htmlform/" +
                "enterHtmlFormWithStandardUi.page?" + PATIENT_UUID_PARAM + "=" + PATIENT_UUID_PARAM +
                "&" + VISIT_UUID_PARAM + "=" + VISIT_UUID_PARAM +
                "&definitionUiResource=referenceapplication:htmlforms/simpleVisitNote.xml";
    }

    private static String getVisitFormUriInvalidValue() {
        return "/////" + VISIT_FORM_URI_DEFAULT_VALUE;
    }
}
