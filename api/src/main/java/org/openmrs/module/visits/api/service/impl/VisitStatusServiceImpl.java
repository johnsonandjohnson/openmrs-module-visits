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
import org.openmrs.module.visits.api.dao.VisitStatusDAO;
import org.openmrs.module.visits.api.entity.VisitStatus;
import org.openmrs.module.visits.api.service.VisitStatusService;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class VisitStatusServiceImpl extends BaseOpenmrsService implements VisitStatusService {

  private VisitStatusDAO visitStatusDAO;

  public void setVisitStatusDAO(VisitStatusDAO visitStatusDAO) {
    this.visitStatusDAO = visitStatusDAO;
  }

  @Transactional(readOnly = true)
  @Override
  public VisitStatus getVisitStatusById(Integer visitStatusId) {
    return visitStatusDAO.getVisitStatusById(visitStatusId);
  }

  @Transactional(readOnly = true)
  @Override
  public VisitStatus getVisitStatusByUuid(String visitStatusUuid) {
    return visitStatusDAO.getVisitStatusByUuid(visitStatusUuid);
  }

  @Transactional(readOnly = true)
  @Override
  public VisitStatus getVisitStatusByName(String visitStatusName) {
    return visitStatusDAO.getVisitStatusByName(visitStatusName);
  }

  @Transactional(readOnly = true)
  @Override
  public List<VisitStatus> getVisitStatusesByGroup(String groupName) {
    return visitStatusDAO.getVisitStatusByGroup(groupName);
  }

  @Transactional(readOnly = true)
  @Override
  public List<VisitStatus> getAllVisitStatuses(boolean includeRetired) {
    return visitStatusDAO.getAllVisitStatuses(includeRetired);
  }

  @Transactional(readOnly = true)
  @Override
  public long getVisitStatusesCount(boolean includeRetired) {
    return visitStatusDAO.getVisitStatusesCount(includeRetired);
  }

  @Transactional
  @Override
  public VisitStatus saveVisitStatus(VisitStatus visitStatus) {
    return visitStatusDAO.saveVisitStatus(visitStatus);
  }

  @Transactional
  @Override
  public VisitStatus retireVisitStatus(VisitStatus visitStatus, String reason) {
    // fields set by OpenMRS AOP classes
    return visitStatusDAO.saveVisitStatus(visitStatus);
  }

  @Transactional
  @Override
  public VisitStatus unretireVisitStatus(VisitStatus visitStatus) {
    // fields set by OpenMRS AOP classes
    return visitStatusDAO.saveVisitStatus(visitStatus);
  }

  @Transactional
  @Override
  public void purgeVisitStatus(VisitStatus visitStatus) {
    visitStatusDAO.deleteVisitStatus(visitStatus);
  }
}
