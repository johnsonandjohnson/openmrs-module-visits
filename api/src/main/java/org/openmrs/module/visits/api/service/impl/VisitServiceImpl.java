/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.dto.VisitDateDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.OverviewCriteria;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements methods for creating, reading, updating and deleting Visit entities
 */
public class VisitServiceImpl extends BaseOpenmrsDataService<Visit> implements VisitService {

  private static final Log LOGGER = LogFactory.getLog(VisitServiceImpl.class);

  private static final Integer BATCH_SIZE = 25;

  private PatientService patientService;

  private LocationService locationService;

  private VisitMapper visitMapper;

  private ConfigService configService;

  @Override
  public List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo) {
    Patient patient = patientService.getPatientByUuid(patientUuid);
    if (patient == null) {
      throw new ValidationException(
          String.format("Patient with uuid %s does not exist", patientUuid));
    }
    return findAllByCriteria(new VisitCriteria(patient), pagingInfo);
  }

  @Override
  public void updateVisit(String visitUuid, VisitDTO visitDTO) {
    saveOrUpdate(visitMapper.fromDto(visitDTO.setUuid(visitUuid)));
  }

  @Override
  public void createVisit(VisitDTO visitDTO) {
    VisitDTO clone = new VisitDTO(visitDTO);
    clone.setStatus(configService.getVisitInitialStatus());

    VisitDateDTO visitDateDTO = clone.getVisitDateDTO();
    if (visitDateDTO.getStartDate() == null) {
      visitDateDTO.setStartDate(new Date());
    }

    saveOrUpdate(visitMapper.fromDto(clone));
  }

  @Override
  public void changeStatusForMissedVisits() {
    List<String> statusesEndingVisit = configService.getStatusesEndingVisit();

    List<String> eligibleStatusesToMarkVisitAsMissed =
        getEligibleStatusesToMarkVisitAsMissed(statusesEndingVisit);
    List<Integer> missedVisitsIds = getMissedVisitsIds(statusesEndingVisit);

    processVisitsInBatches(missedVisitsIds, eligibleStatusesToMarkVisitAsMissed);
  }

  @Override
  public List<Visit> getVisitsForLocation(
      String locationUuid,
      PagingInfo pagingInfo,
      String query,
      String visitStatus,
      Long dateFrom,
      Long dateTo,
      String timePeriod) {
    Location location = locationService.getLocationByUuid(locationUuid);
    if (location == null) {
      throw new ValidationException(
          String.format("Location with uuid %s does not exist", locationUuid));
    }
    return findAllByCriteria(
        new OverviewCriteria(location, query, visitStatus, dateFrom, dateTo, timePeriod),
        pagingInfo);
  }

  @Override
  @Transactional
  public void changeVisitStatuses(List<String> visitUuids, String newVisitStatus) {
    int numberOfIterations = (int) Math.ceil((double) visitUuids.size() / BATCH_SIZE);
    for (int i = 0; i < numberOfIterations; i++) {
      clearSessionCache();
      processSingleBatch(getVisitsUuidsBatch(i, visitUuids), newVisitStatus);
    }
  }

  private List<String> getVisitsUuidsBatch(int iterationNumber, List<String> visitUuids) {
    int endIndex = Math.min((iterationNumber + 1) * BATCH_SIZE, visitUuids.size());
    return visitUuids.subList(iterationNumber * BATCH_SIZE, endIndex);
  }

  private void processSingleBatch(List<String> visitUuids, String newVisitStatus) {
    org.openmrs.api.VisitService visitService = Context.getVisitService();
    for (String uuid : visitUuids) {
      Visit visit = visitService.getVisitByUuid(uuid);
      if (visit == null) {
        LOGGER.warn(String.format("Visit with uuid: %s not found", uuid));
        continue;
      }
      VisitDecorator visitDecorator = new VisitDecorator(visit);
      visitDecorator.setStatus(newVisitStatus);
      visitService.saveVisit(visitDecorator.getObject());
    }
  }

  public void setVisitMapper(VisitMapper visitMapper) {
    this.visitMapper = visitMapper;
  }

  public VisitServiceImpl setPatientService(PatientService patientService) {
    this.patientService = patientService;
    return this;
  }

  public void setLocationService(LocationService locationService) {
    this.locationService = locationService;
  }

  public void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  private void processVisitsInBatches(List<Integer> visitsIds, List<String> visitStatuses) {
    String missedVisitStatus = configService.getStatusOfMissedVisit();
    VisitAttributeType visitStatusAttrType =
        Context.getVisitService()
            .getVisitAttributeTypeByUuid(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    int numOfIterations = (int) Math.ceil((float) visitsIds.size() / BATCH_SIZE);
    MissedVisitService missedVisitService = Context.getService(MissedVisitService.class);
    for (int i = 0; i < numOfIterations; i++) {
      clearSessionCache();
      List<Integer> subList = getVisitIdsSubList(i, visitsIds);
      missedVisitService.changeVisitStatusesToMissed(subList, visitStatuses, missedVisitStatus,
          visitStatusAttrType);
    }
  }

  private List<Integer> getVisitIdsSubList(int iterationNumber, List<Integer> visitsIds) {
    int endIndex = Math.min((iterationNumber + 1) * BATCH_SIZE, visitsIds.size());
    return visitsIds.subList(iterationNumber * BATCH_SIZE, endIndex);
  }

  private List<String> getEligibleStatusesToMarkVisitAsMissed(List<String> statusesEndingVisit) {
    return configService.getVisitStatuses().stream()
        .filter(status -> !statusesEndingVisit.contains(status))
        .collect(Collectors.toList());
  }

  private List<Integer> getMissedVisitsIds(List<String> statusesEndingVisit) {
    return findAllByCriteria(
        VisitCriteria.forMissedVisits(
            configService.getMinimumVisitDelayToMarkItAsMissed(),
            configService.getStatusOfMissedVisit(),
            statusesEndingVisit))
        .stream()
        .map(Visit::getVisitId)
        .collect(Collectors.toList());
  }

  private void clearSessionCache() {
    Context.flushSession();
    Context.clearSession();
  }
}
