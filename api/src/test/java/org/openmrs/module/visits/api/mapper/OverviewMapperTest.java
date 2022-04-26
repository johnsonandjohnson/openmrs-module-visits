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
