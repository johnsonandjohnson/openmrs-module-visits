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

import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.MissedVisitService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class MissedVisitServiceImpl extends BaseOpenmrsService implements MissedVisitService {

  private VisitService visitService;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void changeVisitStatusesToMissed(
      List<Integer> visitIds,
      List<String> statusesToMarkVisitAsMissed,
      String missedVisitStatus,
      VisitAttributeType visitStatusAttributeType) {
    visitIds.forEach(
        id ->
            changeVisitStatus(
                id, statusesToMarkVisitAsMissed, missedVisitStatus, visitStatusAttributeType));
  }

  private void changeVisitStatus(
      Integer visitId,
      List<String> statusesToMarkVisitAsMissed,
      String missedVisitStatus,
      VisitAttributeType visitStatusAttributeType) {
    Visit visit = visitService.getVisit(visitId);
    if (visit == null) {
      throw new EntityNotFoundException(String.format("Visit with id %d not found", visitId));
    }
    VisitDecorator visitDecorator = new VisitDecorator(visit);
    if (statusesToMarkVisitAsMissed.contains(visitDecorator.getStatus())) {
      visitDecorator.setVisitStatus(visitStatusAttributeType, missedVisitStatus);
      visitDecorator.setChanged();
      visitService.saveVisit(visitDecorator.getObject());
    }
  }

  public void setVisitService(VisitService visitService) {
    this.visitService = visitService;
  }
}
