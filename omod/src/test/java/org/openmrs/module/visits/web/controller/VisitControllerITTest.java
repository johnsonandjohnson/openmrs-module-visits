package org.openmrs.module.visits.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.VisitDAO;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.web.BaseModuleWebContextSensitiveWithActivatorTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.hamcrest.Matchers.hasItem;
import static org.openmrs.module.visits.api.util.ConfigConstants.PATIENT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_UUID_PARAM;
import static org.openmrs.module.visits.web.PageConstants.PAGE_PARAM;
import static org.openmrs.module.visits.web.PageConstants.ROWS_PARAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public class VisitControllerITTest extends BaseModuleWebContextSensitiveWithActivatorTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    private static final String PATIENT_1_UUID = "42c9cb97-894a-47e6-8321-f0120576a545";
    private static final String PATIENT_2_UUID = "007037a0-0500-11e3-8ffd-0800200c9a66";
    private static final int NON_EXISTING_PATIENT_UUID = 999999;
    private static final int PAGE_1 = 1;
    private static final int PAGE_2 = 2;
    private static final int PAGE_SIZE_2 = 2;
    private static final int FOUR = 4;
    private static final int DEFAULT_ROWS_COUNT = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int BAD_ROWS_COUNT = 0;
    private static final int BAD_PAGE_NUMBER = 0;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("visits.visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("visitDAO")
    private VisitDAO visitDAO;

    @Autowired
    @Qualifier("patientService")
    private PatientService patientService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        executeDataSet(XML_DATA_SET_PATH + "ConceptDataSet.xml");
        executeDataSet(XML_DATA_SET_PATH + "PatientDataSet.xml");
        executeDataSet(XML_DATA_SET_PATH + "ConfigDataSet.xml");
    }

    @Test
    public void shouldReturnAllForPatientOne() throws Exception {
        Visit visit1 = prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit2 = prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit3 = prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit4 = prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_2_UUID);
        prepareVisitForPatient(PATIENT_2_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].uuid")
                    .value(hasItem(visit1.getUuid())))
                .andExpect(jsonPath("$.content.[*].uuid")
                    .value(hasItem(visit2.getUuid())))
                .andExpect(jsonPath("$.content.[*].uuid")
                    .value(hasItem(visit3.getUuid())))
                .andExpect(jsonPath("$.content.[*].uuid")
                    .value(hasItem(visit4.getUuid())))
                .andExpect(jsonPath("$.content.length()").value(FOUR))
                .andReturn();
    }

    @Test
    public void shouldReturnAllForPatientOneForPage1() throws Exception {
        Visit visit1 = prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit2 = prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID)
            .param(PAGE_PARAM, String.valueOf(PAGE_1))
            .param(ROWS_PARAM, String.valueOf(PAGE_SIZE_2)))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].uuid")
                .value(hasItem(visit1.getUuid())))
            .andExpect(jsonPath("$.content.[*].uuid")
                .value(hasItem(visit2.getUuid())))
            .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
            .andReturn();
    }

    @Test
    public void shouldReturnAllForPatientOneForPage2() throws Exception {
        prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit3 = prepareVisitForPatient(PATIENT_1_UUID);
        Visit visit4 = prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID)
            .param(PAGE_PARAM, String.valueOf(PAGE_2))
            .param(ROWS_PARAM, String.valueOf(PAGE_SIZE_2)))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].uuid")
                .value(hasItem(visit3.getUuid())))
            .andExpect(jsonPath("$.content.[*].uuid")
                .value(hasItem(visit4.getUuid())))
            .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
            .andReturn();
    }

    @Test
    public void shouldReturnBadRequestForNonExistingPatient() throws Exception {
        prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", NON_EXISTING_PATIENT_UUID))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();
    }

    @Test
    public void shouldReturnBadRequestForBadPageNumber() throws Exception {
        prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID)
            .param(PAGE_PARAM, String.valueOf(BAD_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT)))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();
    }

    @Test
    public void shouldReturnBadRequestForBadRowsNumber() throws Exception {
        prepareVisitForPatient(PATIENT_1_UUID);
        prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(BAD_ROWS_COUNT)))
            .andReturn();
    }

    @Test
    public void shouldReturnValidVisitFormUriAlongWithDTO() throws Exception {
        Visit visit = prepareVisitForPatient(PATIENT_1_UUID);
        mockMvc.perform(get("/visits/patient/{uuid}", PATIENT_1_UUID)
                .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
                .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].uuid")
                        .value(hasItem(visit.getUuid())))
                .andExpect(jsonPath("$.content.[*].formUri")
                        .value(hasItem(getExpectedUri(visit.getPatient().getUuid(), visit.getUuid()))))
                .andReturn();
    }

    private Visit prepareVisitForPatient(String patientUuid) {
        VisitType visitType = new VisitType("type", "type");
        visitType = visitDAO.saveVisitType(visitType);
        return visitDAO.saveVisit(
            new Visit(patientService.getPatientByUuid(patientUuid),
            visitType,
            new Date())
        );
    }

    private static String getExpectedUri(String patientUuid, String visitUuid) {
        return "/htmlformentryui/htmlform/" +
                "enterHtmlFormWithStandardUi.page?" + PATIENT_UUID_PARAM + "=" + patientUuid +
                "&" + VISIT_UUID_PARAM + "=" + visitUuid +
                "&definitionUiResource=cfl:htmlforms/cfl-visit-note.xml";
    }
}
