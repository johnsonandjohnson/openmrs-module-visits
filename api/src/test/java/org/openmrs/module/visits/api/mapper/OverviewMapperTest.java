/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.dto.OverviewDTO;
import org.openmrs.module.visits.builder.VisitBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OverviewMapperTest extends ContextMockedTest {

  private final OverviewMapper overviewMapper = new OverviewMapper();

  @Test
  public void shouldMapToDto() {
    Visit visit = new VisitBuilder().build();
    OverviewDTO overviewDTO = overviewMapper.toDto(visit);
    assertEquals(
        visit.getPatient().getIdentifiers().toArray()[0].toString(),
        overviewDTO.getPatientIdentifier());
    assertEquals(visit.getLocation().getName(), overviewDTO.getLocation());
    assertEquals(visit.getStartDatetime(), overviewDTO.getStartDate());
    assertEquals(visit.getVisitType().getName(), overviewDTO.getType());
    assertEquals(visit.getUuid(), overviewDTO.getUuid());
    assertEquals(
        visit.getPatient().getPersonName().getFullName(), overviewDTO.getNameUrl().getName());
    assertEquals(
        "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + visit.getPatient().getUuid(),
        overviewDTO.getNameUrl().getUrl());
    assertNull(overviewDTO.getTime());
    assertNull(overviewDTO.getStatus());
    assertNull(overviewDTO.getActualDate());
  }

  @Test(expected = NotImplementedException.class)
  public void shouldFromDto() {
    overviewMapper.fromDto(new OverviewDTO());
  }
}
