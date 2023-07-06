/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.visits.api.dao.VisitTimeDAO;
import org.openmrs.module.visits.api.entity.VisitTime;
import org.openmrs.module.visits.api.service.VisitTimeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class VisitTimeServiceImpl extends BaseOpenmrsService implements VisitTimeService {

  private VisitTimeDAO visitTimeDAO;

  public void setVisitTimeDAO(VisitTimeDAO visitTimeDAO) {
    this.visitTimeDAO = visitTimeDAO;
  }

  @Transactional(readOnly = true)
  @Override
  public VisitTime getVisitTimeById(Integer visitTimeId) {
    return visitTimeDAO.getVisitTimeById(visitTimeId);
  }

  @Transactional(readOnly = true)
  @Override
  public VisitTime getVisitTimeByUuid(String visitTimeUuid) {
    return visitTimeDAO.getVisitTimeByUuid(visitTimeUuid);
  }

  @Transactional(readOnly = true)
  @Override
  public VisitTime getVisitTimeByName(String visitTimeName) {
    return visitTimeDAO.getVisitTimeByName(visitTimeName);
  }

  @Transactional(readOnly = true)
  @Override
  public List<VisitTime> getVisitTimesByGroup(String groupName) {
    return visitTimeDAO.getVisitTimesByGroup(groupName);
  }

  @Transactional(readOnly = true)
  @Override
  public List<VisitTime> getAllVisitTimes(boolean includeRetired) {
    return visitTimeDAO.getAllVisitTimes(includeRetired);
  }

  @Transactional(readOnly = true)
  @Override
  public long getVisitTimesCount(boolean includeRetired) {
    return visitTimeDAO.getVisitTimesCount(includeRetired);
  }

  @Transactional
  @Override
  public VisitTime saveVisitTime(VisitTime visitTime) {
    return visitTimeDAO.saveVisitTime(visitTime);
  }

  @Transactional
  @Override
  public VisitTime retireVisitTime(VisitTime visitTime, String reason) {
    // fields set by OpenMRS AOP classes
    return visitTimeDAO.saveVisitTime(visitTime);
  }

  @Transactional
  @Override
  public VisitTime unretireVisitTime(VisitTime visitTime) {
    // fields set by OpenMRS AOP classes
    return visitTimeDAO.saveVisitTime(visitTime);
  }

  @Transactional
  @Override
  public void purgeVisitTime(VisitTime visitTime) {
    visitTimeDAO.deleteVisitTime(visitTime);
  }
}
