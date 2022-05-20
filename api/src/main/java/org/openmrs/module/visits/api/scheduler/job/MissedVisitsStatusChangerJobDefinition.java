/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.VisitService;

public class MissedVisitsStatusChangerJobDefinition extends JobDefinition {

  private static final Log LOGGER = LogFactory.getLog(MissedVisitsStatusChangerJobDefinition.class);
  private static final String TASK_NAME = "Missed Visits Status Changer";

  @Override
  public void execute() {
    LOGGER.info(getTaskName() + " started");
    getVisitService().changeStatusForMissedVisits();
  }

  @Override
  public String getTaskName() {
    return TASK_NAME;
  }

  @Override
  public boolean shouldStartAtFirstCreation() {
    return true;
  }

  @Override
  public Class<MissedVisitsStatusChangerJobDefinition> getTaskClass() {
    return MissedVisitsStatusChangerJobDefinition.class;
  }

  private VisitService getVisitService() {
    return Context.getRegisteredComponent("visits.visitService", VisitService.class);
  }
}
