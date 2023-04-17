/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.entity;

import org.openmrs.BaseOpenmrsMetadata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "visits.VisitTime")
@Table(name = "visits_visit_time")
public class VisitTime extends BaseOpenmrsMetadata {

  private static final long serialVersionUID = 7264496940047064777L;

  @Id
  @GeneratedValue
  @Column(name = "visit_time_id")
  private Integer visitTimeId;

  @Column(name = "time_group")
  private String timeGroup;

  @Override
  public Integer getId() {
    return visitTimeId;
  }

  @Override
  public void setId(Integer id) {
    this.visitTimeId = id;
  }

  public Integer getVisitTimeId() {
    return visitTimeId;
  }

  public void setVisitTimeId(Integer visitTimeId) {
    this.visitTimeId = visitTimeId;
  }

  public String getTimeGroup() {
    return timeGroup;
  }

  public void setTimeGroup(String timeGroup) {
    this.timeGroup = timeGroup;
  }
}
