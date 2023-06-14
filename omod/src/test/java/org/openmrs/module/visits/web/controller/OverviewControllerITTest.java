/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.visits.web.PageConstants.PAGE_PARAM;
import static org.openmrs.module.visits.web.PageConstants.ROWS_PARAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.VisitDAO;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.OverviewDTO;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.DateUtil;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.dto.PageDTO;
import org.openmrs.module.visits.web.BaseModuleWebContextSensitiveWithActivatorTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@Ignore("Disabled to safe time during overview page refactoring")
public class OverviewControllerITTest extends BaseModuleWebContextSensitiveWithActivatorTest {

  private static final String XML_DATA_SET_PATH = "datasets/";
  private static final String QUERY_PARAM = "query";
  private static final String PATIENT_1_UUID = "42c9cb97-894a-47e6-8321-f0120576a545";
  private static final String PATIENT_2_UUID = "007037a0-0500-11e3-8ffd-0800200c9a66";
  private static final int PAGE_1 = 1;
  private static final int PAGE_2 = 2;
  private static final int PAGE_SIZE_1 = 1;
  private static final int PAGE_SIZE_2 = 2;
  private static final int PAGE_SIZE_4 = 4;
  private static final int FOUR = 4;
  private static final int DEFAULT_ROWS_COUNT = 10;
  private static final int DEFAULT_PAGE_NUMBER = 1;
  private static final int BAD_ROWS_COUNT = 0;
  private static final int BAD_PAGE_NUMBER = 0;
  private static final String LOCATION_1_UUID = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";
  private static final String LOCATION_2_UUID = "3defd5b4-0010-4055-93c8-10419ad1320e";
  private static final int NON_EXISTING_LOCATION_UUID = 999999;
  private static final String VISIT_TYPE_NAME = "type";
  private static final String VISIT_TYPE_DESC = "type";
  private static final String PATIENT_2_IDENTIFIER = "BBC123ABC123";
  private static final String PATIENT_2_FULL_NAME = "Another Sick Person";
  private static final String LOCATION_1_NAME = "Test Parent Location";
  private static final String STATUS_SCHEDULED = "SCHEDULED";
  private static final String TIME_MORNING = "Morning";
  private static final String PATIENT_2_URL =
      "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + PATIENT_2_UUID;
  private static final long TOTAL_RECORDS = 2L;
  private static final String TIME_PERIOD = "timePeriod";
  private static final String TODAY = "today";
  private static final String WEEK = "week";
  private static final String MONTH = "month";
  private static final TypeReference<PageDTO<OverviewDTO>> VISIT_OVERVIEW_DTO_TYPE = new TypeReference<PageDTO<OverviewDTO>>() {
  };

  private static final String VISIT_1_UUID = "16f280e0-3e91-48b4-acb2-c29c30595b45";

  private static final String VISIT_2_UUID = "c558c650-acfc-4e35-9245-ab203074ce42";

  private static final String MISSED_VISIT_STATUS = "MISSED";

  private MockMvc mockMvc;

  private VisitAttributeType statusAttributeType;

  private VisitAttributeType timeAttributeType;

  private ObjectMapper visitMapper;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  @Qualifier("visitDAO")
  private VisitDAO visitDAO;

  @Autowired
  @Qualifier("patientService")
  private PatientService patientService;

  @Autowired
  @Qualifier("locationService")
  private LocationService locationService;

  @Autowired
  private VisitService visitService;

  @Before
  public void setUp() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    executeDataSet(XML_DATA_SET_PATH + "ConceptDataSet.xml");
    executeDataSet(XML_DATA_SET_PATH + "PatientDataSet.xml");
    executeDataSet(XML_DATA_SET_PATH + "LocationDataSet.xml");

    statusAttributeType = visitDAO.getVisitAttributeTypeByUuid(
        ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    timeAttributeType = visitDAO.getVisitAttributeTypeByUuid(
        ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID);

    visitMapper = new ObjectMapper();
  }

  @Test
  public void shouldReturnAllForLocationOne() throws Exception {
    Visit visit1 = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit3 = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit4 = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_2_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_2_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit1.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit3.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit4.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(FOUR))
        .andReturn();
  }

  @Test
  public void shouldReturnAllForLocationOneForPage1() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(PAGE_1))
            .param(ROWS_PARAM, String.valueOf(PAGE_SIZE_2)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllForLocationOneForPage2() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(PAGE_2))
            .param(ROWS_PARAM, String.valueOf(PAGE_SIZE_2)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnBadRequestForNonExistingLocation() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", NON_EXISTING_LOCATION_UUID))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andReturn();
  }

  @Test
  public void shouldReturnBadRequestForBadPageNumber() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(BAD_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andReturn();
  }

  @Test
  public void shouldReturnBadRequestForBadRowsNumber() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(BAD_ROWS_COUNT)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("{\"error\":\"Invalid page size 0\",\"errorMessages\":null}"))
        .andReturn();
  }

  @Test
  public void shouldReturnValidVisitOverview() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(PAGE_1))
            .param(ROWS_PARAM, String.valueOf(PAGE_SIZE_2)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].patientIdentifier").value(hasItem(PATIENT_2_IDENTIFIER)))
        .andExpect(jsonPath("$.content.[*].nameUrl.name").value(hasItem(PATIENT_2_FULL_NAME)))
        .andExpect(jsonPath("$.content.[*].nameUrl.url").value(hasItem(PATIENT_2_URL)))
        .andExpect(
            jsonPath("$.content.[*].startDate").value(hasItem(visit.getStartDatetime().getTime())))
        .andExpect(jsonPath("$.content.[*].time").value(hasItem(TIME_MORNING)))
        .andExpect(jsonPath("$.content.[*].type").value(hasItem(VISIT_TYPE_NAME)))
        .andExpect(jsonPath("$.content.[*].status").value(hasItem(STATUS_SCHEDULED)))
        .andExpect(jsonPath("$.content.[*].location").value(hasItem(LOCATION_1_NAME)))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_1))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithGivenNameContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Another";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithMiddleNameContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Sick";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithFamilyNameContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Person";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithPartOfAGivenNameContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Anot";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithIdentifierContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "BBC123ABC123";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllWithPartOfIdentifierContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "123AB";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnPatientSecondWithGivenNameContainingSearchTerm() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Anot";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnAllPatientsWithFamilyNameContainingSearchTerm() throws Exception {
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit3 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit4 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Person";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit3.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit4.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_4))
        .andReturn();
  }

  @Test
  public void shouldReturnPatientSecondWithGivenNameAndMiddleNameAndFamilyNameContainingSearchTerm()
      throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Another Sick Person";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnPatientSecondWithGivenNameAndMiddleNameAndFamilyNameContainingSearchTermManySpaces()
      throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Another   Sick  Person";
    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visit2.getUuid())))
        .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE_2))
        .andReturn();
  }

  @Test
  public void shouldReturnExpectedStructureOfResponse() throws Exception {
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visit = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    Visit visit2 = prepareVisitForPatientWithLocation(PATIENT_2_UUID, LOCATION_1_UUID);
    final String searchTerm = "Another   Sick  Person";
    MvcResult result = mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(QUERY_PARAM, searchTerm))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andReturn();

    assertThat(result, is(notNullValue()));
    PageDTO<Visit> expected =
        new PageDTO<>(Arrays.asList(visit, visit2),
            new PagingInfo(DEFAULT_PAGE_NUMBER, DEFAULT_ROWS_COUNT));
    expected.setTotalRecords(TOTAL_RECORDS);
    PageDTO<OverviewDTO> actual = getPageOverviewDTO(result);
    assertThat(actual.getPageIndex(), is(expected.getPageIndex()));
    assertThat(actual.getPageCount(), is(expected.getPageCount()));
    assertThat(actual.getPageSize(), is(expected.getPageSize()));
    assertThat(actual.getContentSize(), is(expected.getContentSize()));
    assertThat(actual.getTotalRecords(), is(expected.getTotalRecords()));
    assertThat(actual.getContent().size(), is(expected.getContent().size()));
  }

  @Test
  public void shouldReturnAllScheduledVisitForToday() throws Exception {
    Visit visitToday = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    Visit visitThreeDaysLater = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    visitThreeDaysLater.setStartDatetime(DateUtil.getDatePlusDays(DateUtil.now(), 3));

    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(TIME_PERIOD, TODAY))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visitToday.getUuid())))
        .andExpect(
            jsonPath("$.content.[*].uuid").value(not(hasItem(visitThreeDaysLater.getUuid()))))
        .andReturn();
  }

  @Test
  public void shouldReturnAllScheduledVisitForWeek() throws Exception {
    Visit visitTomorrow = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    visitTomorrow.setStartDatetime(DateUtil.getDatePlusDays(DateUtil.now(), 1));
    Visit visitTwoWeeksLater = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    visitTwoWeeksLater.setStartDatetime(DateUtil.getDatePlusDays(DateUtil.now(), 14));

    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(TIME_PERIOD, WEEK))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visitTomorrow.getUuid())))
        .andExpect(jsonPath("$.content.[*].uuid").value(not(hasItem(visitTwoWeeksLater.getUuid()))))
        .andReturn();
  }

  @Test
  public void shouldReturnAllScheduledVisitForMonth() throws Exception {
    Visit visitWeekLater = prepareVisitForPatientWithLocation(PATIENT_1_UUID, LOCATION_1_UUID);
    visitWeekLater.setStartDatetime(DateUtil.getDatePlusDays(DateUtil.now(), 7));
    Visit visitThreeMonthsLater = prepareVisitForPatientWithLocation(PATIENT_1_UUID,
        LOCATION_1_UUID);
    visitThreeMonthsLater.setStartDatetime(DateUtil.getDatePlusMonths(DateUtil.now(), 3));

    mockMvc
        .perform(get("/visits/overview/{uuid}", LOCATION_1_UUID)
            .param(PAGE_PARAM, String.valueOf(DEFAULT_PAGE_NUMBER))
            .param(ROWS_PARAM, String.valueOf(DEFAULT_ROWS_COUNT))
            .param(TIME_PERIOD, MONTH))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.[*].uuid").value(hasItem(visitWeekLater.getUuid())))
        .andExpect(
            jsonPath("$.content.[*].uuid").value(not(hasItem(visitThreeMonthsLater.getUuid()))))
        .andReturn();
  }

  @Test
  public void shouldUpdateVisitStatusesFromScheduledToMissed() throws Exception {
    executeDataSet(XML_DATA_SET_PATH + "VisitDataSet.xml");

    mockMvc.perform(post("/visits/overview/updateVisitStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(prepareRequestBody())
            .param("newVisitStatus", MISSED_VISIT_STATUS))
        .andExpect(status().is(HttpStatus.OK.value()));

    List<Visit> updatedVisits = visitService.getAllVisits().stream()
        .filter(v -> Arrays.asList(VISIT_1_UUID, VISIT_2_UUID).contains(v.getUuid())).collect(
            Collectors.toList());

    updatedVisits.forEach(
        visit -> assertEquals(MISSED_VISIT_STATUS, new VisitDecorator(visit).getStatus()));
  }

  private String prepareRequestBody() throws Exception {
    String[] values = new String[]{VISIT_1_UUID, VISIT_2_UUID};
    return new ObjectMapper().writeValueAsString(values);
  }

  private PageDTO<OverviewDTO> getPageOverviewDTO(MvcResult result) throws java.io.IOException {
    return visitMapper.readValue(result.getResponse().getContentAsString(),
        VISIT_OVERVIEW_DTO_TYPE);
  }

  private Visit prepareVisitForPatientWithLocation(String patientUuid, String locationUuid) {
    VisitType visitType = new VisitType(VISIT_TYPE_NAME, VISIT_TYPE_DESC);
    visitType = visitDAO.saveVisitType(visitType);
    Visit visit = new Visit(patientService.getPatientByUuid(patientUuid), visitType, new Date());
    visit.setLocation(locationService.getLocationByUuid(locationUuid));
    VisitAttribute statusAttribute = createVisitAttribute(statusAttributeType, STATUS_SCHEDULED);
    VisitAttribute timeAttribute = createVisitAttribute(timeAttributeType, TIME_MORNING);
    visit.addAttribute(statusAttribute);
    visit.addAttribute(timeAttribute);
    return visitDAO.saveVisit(visit);
  }

  private VisitAttribute createVisitAttribute(VisitAttributeType type, String value) {
    VisitAttribute attribute = new VisitAttribute();
    attribute.setValueReferenceInternal("test");
    attribute.setAttributeType(type);
    attribute.setValue(value);
    return attribute;
  }
}
