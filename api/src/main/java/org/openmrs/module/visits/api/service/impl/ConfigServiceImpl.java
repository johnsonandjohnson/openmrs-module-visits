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

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.dto.VisitFormUrisMap;
import org.openmrs.module.visits.api.entity.VisitStatus;
import org.openmrs.module.visits.api.entity.VisitTime;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.VisitStatusService;
import org.openmrs.module.visits.api.service.VisitTimeService;
import org.openmrs.module.visits.api.util.GPDefinition;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtil;

/** Provides the default implementation of module configuration set */
public class ConfigServiceImpl implements ConfigService {

  private static final Log LOGGER = LogFactory.getLog(ConfigServiceImpl.class);

  private static final String COMMA_DELIMITER = ",";
  private static final int MINIMUM_DAYS_NUMBER_OF_VISIT_DELAY = 1;

  @Override
  public List<String> getVisitTimes() {
    VisitTimeService visitTimeService = Context.getService(VisitTimeService.class);
    List<VisitTime> visitTimes = visitTimeService.getAllVisitTimes(false);

    return visitTimes.stream().map(BaseOpenmrsMetadata::getName).collect(Collectors.toList());
  }

  @Override
  public List<String> getVisitStatuses() {
    VisitStatusService visitStatusService = Context.getService(VisitStatusService.class);
    List<VisitStatus> visitStatuses = visitStatusService.getAllVisitStatuses(false);

    return visitStatuses.stream().map(BaseOpenmrsMetadata::getName).collect(Collectors.toList());
  }

  @Override
  public int getMinimumVisitDelayToMarkItAsMissed() {
    GPDefinition gpDefinition = GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED;
    int days = GlobalPropertyUtil.parseInt(gpDefinition.getKey(), getGp(gpDefinition));
    if (days < MINIMUM_DAYS_NUMBER_OF_VISIT_DELAY) {
      LOGGER.warn(
          String.format(
              "The GP minimumVisitDelayToMarkItAsMissed could not be below 1 "
                  + "(the current value: %d). The value 1 will be used",
              days));
      days = MINIMUM_DAYS_NUMBER_OF_VISIT_DELAY;
    }
    return days;
  }

  @Override
  public String getVisitInitialStatus() {
    List<VisitStatus> visitStatuses =
        Context.getService(VisitStatusService.class).getAllVisitStatuses(false);
    if (CollectionUtils.isEmpty(visitStatuses)) {
      return null;
    }

    VisitStatus initialStatus =
        visitStatuses.stream()
            .filter(VisitStatus::getDefault)
            .findFirst()
            .orElse(visitStatuses.get(0));

    return initialStatus.getName();
  }

  @Override
  public List<String> getStatusesEndingVisit() {
    List<String> statusesEndingVisits =
        GlobalPropertyUtil.parseList(
            getGp(GlobalPropertiesConstants.STATUSES_ENDING_VISIT), COMMA_DELIMITER);
    List<String> visitStatuses = getVisitStatuses();
    if (!visitStatuses.containsAll(statusesEndingVisits)) {
      LOGGER.warn(
          String.format(
              "Not all statuses ending a visit defined in GP (%s) are part of visit statuses (%s)",
              statusesEndingVisits, visitStatuses));
    }
    return statusesEndingVisits;
  }

  @Override
  public String getStatusOfMissedVisit() {
    String missedStatus = getGp(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT);
    List<String> visitStatuses = getVisitStatuses();
    if (!visitStatuses.contains(missedStatus)) {
      LOGGER.warn(
          String.format(
              "The missed visit's status defined in GP (%s) is not part of visit statuses (%s)",
              missedStatus, visitStatuses));
    }
    return missedStatus;
  }

  @Override
  public String getStatusOfOccurredVisit() {
    String missedStatus = getGp(GlobalPropertiesConstants.STATUS_OF_OCCURRED_VISIT);
    List<String> visitStatuses = getVisitStatuses();
    if (!visitStatuses.contains(missedStatus)) {
      LOGGER.warn(
          String.format(
              "The occurred visit's status defined in GP (%s) is not part of visit statuses (%s)",
              missedStatus, visitStatuses));
    }
    return missedStatus;
  }

  @Override
  public VisitFormUrisMap getVisitFormUrisMap() {
    return new VisitFormUrisMap(getGp(GlobalPropertiesConstants.VISIT_FORM_URIS_KEY));
  }

  @Override
  public boolean isEncounterDatetimeValidationEnabled() {
    String settingValue = getGp(GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION);
    return GlobalPropertyUtil.parseBool(settingValue);
  }

  @Override
  public boolean isMissedVisitChangerJobShouldBeCreated() {
    String gpValue = getGp(GlobalPropertiesConstants.MISSED_VISIT_CHANGER_CREATION_GP.getKey());
    return GlobalPropertyUtil.parseBool(gpValue);
  }

  private String getGp(GPDefinition gpDefinition) {
    return getGp(gpDefinition.getKey());
  }

  private String getGp(String key) {
    return Context.getAdministrationService().getGlobalProperty(key);
  }
}
