package org.openmrs.module.visits.api.mapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.dto.VisitDateDTO;
import org.openmrs.module.visits.api.dto.VisitDetailsDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.service.ConfigService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/** Maps objects between the related visit types */
public class VisitMapper extends AbstractMapper<VisitDTO, Visit> {

  private static final Log LOGGER = LogFactory.getLog(VisitMapper.class);

  private VisitService visitService;

  private LocationService locationService;

  private ConfigService configService;

  private PatientService patientService;

  @Override
  public VisitDTO toDto(Visit visit) {
    VisitDecorator visitDecorator = new VisitDecorator(visit);
    VisitDateDTO visitDateDTO =
        new VisitDateDTO(
            visit.getStartDatetime(), visitDecorator.getTime(), visitDecorator.getActualDate());
    String stringUri = null;
    try {
      stringUri = configService.getVisitFormUrisMap().getUri(visit);
    } catch (ValidationException e) {
      LOGGER.error(e.getErrorResponse().getError());
    }

    return new VisitDTO()
        .setUuid(visit.getUuid())
        .setStartDate(visit.getStartDatetime())
        .setTime(visitDecorator.getTime())
        .setLocation(visit.getLocation() == null ? null : visit.getLocation().getUuid())
        .setType(visit.getVisitType() == null ? null : visit.getVisitType().getUuid())
        .setStatus(visitDecorator.getStatus())
        .setFormUri(stringUri)
        .setActualDate(visitDecorator.getActualDate())
        .setPatientUuid(visit.getPatient() == null ? null : visit.getPatient().getUuid())
        .setVisitDateDTO(visitDateDTO);
  }

  @Override
  public Visit fromDto(VisitDTO dto) {
    Visit result = visitService.getVisitByUuid(dto.getUuid());
    if (result == null) {
      result = new Visit();
    } else {
      result.setDateChanged(new Date());
      result.setChangedBy(Context.getAuthenticatedUser());
    }
    result.setPatient(patientService.getPatientByUuid(dto.getPatientUuid()));

    VisitDecorator resultDecorator = new VisitDecorator(result);
    resultDecorator.setStartDatetime(dto.getVisitDateDTO().getStartDate());
    resultDecorator.setLocation(locationService.getLocationByUuid(dto.getLocation()));
    resultDecorator.setVisitType(visitService.getVisitTypeByUuid(dto.getType()));
    resultDecorator.setStatus(dto.getStatus());
    resultDecorator.setTime(dto.getVisitDateDTO().getTime());
    resultDecorator.setActualDate(dto.getVisitDateDTO().getActualDate());
    return resultDecorator.getObject();
  }

  public VisitDetailsDTO toDtoWithDetails(Visit visit) {
    return new VisitDetailsDTO(
        toDto(visit),
        visit.getLocation() == null ? null : visit.getLocation().getName(),
        visit.getVisitType() == null ? null : visit.getVisitType().getName());
  }

  public List<VisitDetailsDTO> toDtosWithDetails(Collection<Visit> visits) {
    List<VisitDetailsDTO> dtos = new ArrayList<>(visits.size());
    for (Visit visit : visits) {
      dtos.add(this.toDtoWithDetails(visit));
    }

    return dtos;
  }

  public void setVisitService(VisitService visitService) {
    this.visitService = visitService;
  }

  public void setLocationService(LocationService locationService) {
    this.locationService = locationService;
  }

  public void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  public void setPatientService(PatientService patientService) {
    this.patientService = patientService;
  }
}
