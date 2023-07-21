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

import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.openmrs.module.visits.api.entity.VisitStatus;

public class VisitStatusDAO extends BaseOpenMRSEntityDAO<VisitStatus> {

  VisitStatusDAO() {
    super(VisitStatus.class);
  }

  public VisitStatus getVisitStatusById(Integer visitStatusId) {
    return getById(visitStatusId);
  }

  public VisitStatus getVisitStatusByUuid(String visitStatusUuid) {
    return getByUuid(visitStatusUuid);
  }

  public VisitStatus getVisitStatusByName(String visitStatusName) {
    return getByName(visitStatusName);
  }
  
  public List<VisitStatus> getVisitStatusByGroup(String groupName) {
    String sql = "FIND_IN_SET(?, status_group) > 0";
    return getSession()
        .createCriteria(VisitStatus.class)
        .add(Restrictions.sqlRestriction(sql, groupName, StringType.INSTANCE))
        .list();
  }

  public List<VisitStatus> getAllVisitStatuses(boolean includeRetired) {
    return getAll(includeRetired);
  }

  public long getVisitStatusesCount(boolean includeRetired) {
    return countAll(includeRetired);
  }

  public VisitStatus saveVisitStatus(VisitStatus visitStatus) {
    return save(visitStatus);
  }

  public void deleteVisitStatus(VisitStatus visitStatus) {
    delete(visitStatus);
  }
}
