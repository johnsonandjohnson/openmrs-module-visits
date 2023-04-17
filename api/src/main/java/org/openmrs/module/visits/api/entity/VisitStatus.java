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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.openmrs.BaseOpenmrsMetadata;

@Entity(name = "visits.VisitStatus")
@Table(name = "visits_visit_status")
public class VisitStatus extends BaseOpenmrsMetadata {

  private static final long serialVersionUID = -8904773388251641608L;

  @Id
  @GeneratedValue
  @Column(name = "visit_status_id")
  private Integer visitStatusId;

  @Column(name = "status_group")
  private String statusGroup;

  @Column(name = "is_default")
  private Boolean isDefault;

  @Override
  public Integer getId() {
    return visitStatusId;
  }

  @Override
  public void setId(Integer id) {
    this.visitStatusId = id;
  }

  public Integer getVisitStatusId() {
    return visitStatusId;
  }

  public void setVisitStatusId(Integer visitStatusId) {
    this.visitStatusId = visitStatusId;
  }

  public String getStatusGroup() {
    return statusGroup;
  }

  public void setStatusGroup(String statusGroup) {
    this.statusGroup = statusGroup;
  }

  public Boolean getDefault() {
    return isDefault;
  }

  public void setDefault(Boolean aDefault) {
    isDefault = aDefault;
  }
}
