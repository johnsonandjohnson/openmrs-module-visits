/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.visits.api.util.TestConstants.VISIT_URI_MAP_JSON;
import static org.powermock.api.mockito.PowerMockito.doReturn;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.openmrs.module.visits.api.entity.VisitStatus;
import org.openmrs.module.visits.api.service.impl.ConfigServiceImpl;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;

public class ConfigServiceTest extends ContextMockedTest {

  private ConfigService configService = new ConfigServiceImpl();

  @Test
  public void shouldReturnVisitStatuses() {
    List<VisitStatus> statuses =
        Arrays.asList(
            buildVisitStatus("SCHEDULED"),
            buildVisitStatus("OCCURRED"),
            buildVisitStatus("MISSED"));
    doReturn(statuses).when(getVisitStatusService()).getAllVisitStatuses(false);

    List<String> visitStatuses = configService.getVisitStatuses();
    assertThat(
        visitStatuses, contains(statuses.stream().map(BaseOpenmrsMetadata::getName).toArray()));
  }

  @Test
  public void shouldReturnMinimumVisitDelayToMarkItAsMissed() {
    doReturn("2")
        .when(getAdministrationService())
        .getGlobalProperty(
            GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED.getKey());

    int delay = configService.getMinimumVisitDelayToMarkItAsMissed();
    assertThat(delay, equalTo(2));
  }

  @Test
  public void shouldReturnVisitInitialStatus() {
    List<VisitStatus> visitStatuses =
        Arrays.asList(
            buildVisitStatus("SCHEDULED"),
            buildVisitStatus("OCCURRED"),
            buildVisitStatus("MISSED"));
    doReturn(visitStatuses).when(getVisitStatusService()).getAllVisitStatuses(false);

    String initialStatus = configService.getVisitInitialStatus();
    assertThat(initialStatus, equalTo(visitStatuses.get(0).getName()));
  }

  @Test
  public void shouldReturnStatusesEndingVisit() {
    String endingStatuses = GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getDefaultValue();
    doReturn(endingStatuses)
        .when(getAdministrationService())
        .getGlobalProperty(GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getKey());

    List<String> endingStatusesList = configService.getStatusesEndingVisit();
    assertThat(endingStatusesList, contains(endingStatuses.split(",")));
  }

  @Test
  public void shouldReturnStatusOfMissedVisit() {
    String missedStatus = GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getDefaultValue();
    doReturn(missedStatus)
        .when(getAdministrationService())
        .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());

    String statusOfMissedVisit = configService.getStatusOfMissedVisit();
    assertThat(statusOfMissedVisit, equalTo(missedStatus));
  }

  @Test
  public void shouldReturnStatusOfOccurredVisit() {
    String occurredStatus = GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT.getDefaultValue();
    doReturn(occurredStatus)
        .when(getAdministrationService())
        .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT.getKey());

    String statusOfOccurredVisit = configService.getStatusOfOccurredVisit();
    assertThat(statusOfOccurredVisit, equalTo(occurredStatus));
  }

  @Test
  public void shouldReturnVisitFormUrisMap() {
    doReturn(VISIT_URI_MAP_JSON)
        .when(getAdministrationService())
        .getGlobalProperty(GlobalPropertiesConstants.VISIT_FORM_URIS_KEY);

    VisitFormUrisMap urisMap = configService.getVisitFormUrisMap();
    assertNotNull(urisMap);
  }

  @Test
  public void shouldReturnIfIsEncounterDatetimeValidationEnabled() {
    String isValidationEnabledString =
        GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION.getDefaultValue();
    doReturn(isValidationEnabledString)
        .when(getAdministrationService())
        .getGlobalProperty(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());

    boolean isEnabled = configService.isEncounterDatetimeValidationEnabled();
    assertThat(isEnabled, equalTo(false));
  }

  private VisitStatus buildVisitStatus(String name) {
    VisitStatus visitStatus = new VisitStatus();
    visitStatus.setName(name);
    visitStatus.setDefault(false);
    return visitStatus;
  }
}
