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

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.visits.api.entity.VisitTime;

import java.util.List;

/** Provides basic methods related to {@link VisitTime} entity. */
public interface VisitTimeService extends OpenmrsService {

  /**
   * Gets visit time by id
   *
   * @param visitTimeId id of visit time
   * @return {@link VisitTime} object
   */
  VisitTime getVisitTimeById(Integer visitTimeId);

  /**
   * Gets visit time by uuid
   *
   * @param visitTimeUuid uuid of visit time
   * @return {@link VisitTime} object
   */
  VisitTime getVisitTimeByUuid(String visitTimeUuid);

  /**
   * Gets visit time by name
   *
   * @param visitTimeName name of visit time
   * @return {@link VisitTime} object
   */
  VisitTime getVisitTimeByName(String visitTimeName);

  /**
   * Gets list of visit times belonging to group
   *
   * @param groupName name of group
   * @return list of {@link  VisitTime} objects
   */
  List<VisitTime> getVisitTimesByGroup(String groupName);

  /**
   * Gets all visit times
   *
   * @param includeRetired determines if result should contain retired visit times or not
   * @return list of {@link  VisitTime} objects
   */
  List<VisitTime> getAllVisitTimes(boolean includeRetired);

  /**
   * Gets number of visit times
   *
   * @param includeRetired determines if result should contain retired visit times or not
   * @return number of visit times
   */
  long getVisitTimeCount(boolean includeRetired);

  /**
   * Saves visit time
   *
   * @param visitTime {@link VisitTime} object to save
   * @return {@link VisitTime} object that is going to be saved
   */
  VisitTime saveVisitTime(VisitTime visitTime);

  /**
   * Retires visit time
   *
   * @param visitTime {@link VisitTime} object to retire
   * @param reason retire reason
   * @return {@link VisitTime} object that is going to be retired
   */
  VisitTime retireVisitTime(VisitTime visitTime, String reason);

  /**
   * Unretires visit time
   *
   * @param visitTime {@link VisitTime} object to unretire
   * @return {@link VisitTime} object that is going to be unretired
   */
  VisitTime unretireVisitTime(VisitTime visitTime);

  /**
   * Purges visit time
   *
   * @param visitTime {@link VisitTime} object to purge
   */
  void purgeVisitTime(VisitTime visitTime);
}
