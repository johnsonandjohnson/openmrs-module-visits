/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import java.util.List;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.visits.api.entity.VisitStatus;

/** Provides basic methods related to {@link VisitStatus} entity. */
public interface VisitStatusService extends OpenmrsService {

  /**
   * Gets visit status by id
   *
   * @param visitStatusId id of visit time
   * @return {@link VisitStatus} object
   */
  VisitStatus getVisitStatusById(Integer visitStatusId);

  /**
   * Gets visit status by uuid
   *
   * @param visitStatusUuid uuid of visit time
   * @return {@link VisitStatus} object
   */
  VisitStatus getVisitStatusByUuid(String visitStatusUuid);

  /**
   * Gets visit status by name
   *
   * @param visitStatusName name of visit time
   * @return {@link VisitStatus} object
   */
  VisitStatus getVisitStatusByName(String visitStatusName);

  /**
   * Gets list of visit statuses belonging to group
   *
   * @param groupName name of group
   * @return list of {@link VisitStatus} objects
   */
  List<VisitStatus> getVisitStatusesByGroup(String groupName);

  /**
   * Gets all visit statuses
   *
   * @param includeRetired determines if result should contain retired visit statuses or not
   * @return list of {@link VisitStatus} objects
   */
  List<VisitStatus> getAllVisitStatuses(boolean includeRetired);

  /**
   * Gets number of visit statuses
   *
   * @param includeRetired determines if result should contain retired visit statuses or not
   * @return number of visit statuses
   */
  long getVisitStatusesCount(boolean includeRetired);

  /**
   * Saves visit status
   *
   * @param visitStatus {@link VisitStatus} object to save
   * @return {@link VisitStatus} object that is going to be saved
   */
  VisitStatus saveVisitStatus(VisitStatus visitStatus);

  /**
   * Retires visit status
   *
   * @param visitStatus {@link VisitStatus} object to retire
   * @param reason retire reason
   * @return {@link VisitStatus} object that is going to be retired
   */
  VisitStatus retireVisitStatus(VisitStatus visitStatus, String reason);

  /**
   * Unretires visit status
   *
   * @param visitStatus {@link VisitStatus} object to unretire
   * @return {@link VisitStatus} object that is going to be unretired
   */
  VisitStatus unretireVisitStatus(VisitStatus visitStatus);

  /**
   * Purges visit status
   *
   * @param visitStatus {@link VisitStatus} object to purge
   */
  void purgeVisitStatus(VisitStatus visitStatus);
}
