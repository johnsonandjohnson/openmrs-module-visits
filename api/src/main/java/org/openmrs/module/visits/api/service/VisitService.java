/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import org.openmrs.Visit;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.domain.PagingInfo;

import java.util.List;

/**
 * Provides methods for creating, reading, updating and deleting Visit entities
 */
public interface VisitService extends BaseOpenmrsCriteriaDataService<Visit> {

  /**
   * Finds paginated collection of the visits for the given patient
   *
   * @param patientUuid uuid of the patient
   * @param pagingInfo  properties of the pagination
   * @return list of the patient's visits, implicitly paginated
   */
  List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo);

  /**
   * Finds paginated collection of the visits for the given query.
   *
   * @return list of the visits, implicitly paginated
   */
  List<Visit> getVisits(VisitSimpleQuery query);

  /**
   * Updates a visit
   *
   * @param visitUuid visit uuid
   * @param visitDTO  visit DTO object
   */
  void updateVisit(String visitUuid, VisitDTO visitDTO);

  /**
   * Changes statuses of eligible visits to missed
   *
   * <p>This method is executed when MissedVisitStatusChanger job is run. Eligible visits for
   * changing status are collected and then statues are changed. Because of efficiency visits are
   * processed in batches
   */
  void changeStatusForMissedVisits();

  /**
   * Creates a visit
   *
   * @param visit visit DTO object
   * @return new Visit, never null
   */
  Visit createVisit(VisitDTO visit);

  /**
   * Changes visits statuses with new provided visit status.
   *
   * @param visitUuids     list of visit uuids for which status will be changed
   * @param newVisitStatus new visit status that will be set for visits
   */
  void changeVisitStatuses(List<String> visitUuids, String newVisitStatus);

}
