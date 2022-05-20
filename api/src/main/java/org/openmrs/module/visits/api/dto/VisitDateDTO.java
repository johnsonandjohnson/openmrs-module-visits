/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO object containing fields related to visit date and time. Created mainly for splitting big
 * constructor in {@link VisitDTO} to avoid SonarQube issues.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDateDTO implements Serializable {

  private static final long serialVersionUID = 14L;

  private Date startDate;

  private String time;

  private Date actualDate;

  public VisitDateDTO() {}

  public VisitDateDTO(Date startDate, String time, Date actualDate) {
    this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
    this.time = time;
    this.actualDate = actualDate != null ? new Date(actualDate.getTime()) : null;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Date getActualDate() {
    return actualDate;
  }

  public void setActualDate(Date actualDate) {
    this.actualDate = actualDate != null ? new Date(actualDate.getTime()) : null;
  }
}
