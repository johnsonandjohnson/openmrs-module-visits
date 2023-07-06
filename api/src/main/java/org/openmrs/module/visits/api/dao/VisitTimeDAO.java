/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.dao;

import org.hibernate.criterion.Restrictions;
import org.openmrs.module.visits.api.entity.VisitStatus;
import org.openmrs.module.visits.api.entity.VisitTime;
import java.util.List;

public class VisitTimeDAO extends BaseOpenMRSEntityDAO<VisitTime> {

  VisitTimeDAO() {
    super(VisitTime.class);
  }

  public VisitTime getVisitTimeById(Integer visitTimeId) {
    return getById(visitTimeId);
  }

  public VisitTime getVisitTimeByUuid(String visitTimeUuid) {
    return getByUuid(visitTimeUuid);
  }

  public VisitTime getVisitTimeByName(String visitTimeName) {
    return getByName(visitTimeName);
  }

  public List<VisitTime> getVisitTimesByGroup(String groupName) {
    return getSession()
        .createCriteria(VisitStatus.class)
        .add(Restrictions.eq("timeGroup", groupName))
        .list();
  }

  public List<VisitTime> getAllVisitTimes(boolean includeRetired) {
    return getAll(includeRetired);
  }

  public long getVisitTimesCount(boolean includeRetired) {
    return countAll(includeRetired);
  }

  public VisitTime saveVisitTime(VisitTime visitTime) {
    return save(visitTime);
  }

  public void deleteVisitTime(VisitTime visitTime) {
    delete(visitTime);
  }
}
