/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.ConfigService;

/**
 * The VisitStatusUpdateAction Class.
 *
 * <p>The implementation of Post Submission Action which sets the Visits Status (Visit attribute) to
 * correct status after the Visit occurred.
 *
 * <p>Example: <br>
 * my-form.xml: <br>
 * {@code <postSubmissionAction
 * class="org.openmrs.module.visits.api.htmlformentry.action.VisitStatusUpdateAction"/>}
 */
public class VisitStatusUpdateAction implements CustomFormSubmissionAction {
  private static final String CONFIG_SERVICE = "visits.configService";

  private static final Log LOGGER = LogFactory.getLog(VisitStatusUpdateAction.class);

  @Override
  public void applyAction(FormEntrySession formEntrySession) {
    if (formEntrySession.getContext().getVisit() != null) {
      final VisitDecorator visitDecorator =
          new VisitDecorator((Visit) formEntrySession.getContext().getVisit());
      visitDecorator.setStatus(getConfigService().getOccurredVisitStatues().get(0));
      visitDecorator.setChanged();
      Context.getVisitService().saveVisit(visitDecorator.getObject());
      LOGGER.info(
          String.format(
              "Visit with uuid: %s has successfully changed the status.",
              visitDecorator.getUuid()));
    }
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(CONFIG_SERVICE, ConfigService.class);
  }
}
