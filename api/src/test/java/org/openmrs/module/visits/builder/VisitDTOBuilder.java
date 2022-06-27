/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.builder;

import org.openmrs.module.visits.api.dto.VisitDTO;

import java.util.Date;

public class VisitDTOBuilder extends AbstractBuilder<VisitDTO> {

  private String uuid;
  private Date startDate;
  private String location;
  private String type;
  private String status;
  private String formUri;
  private Date actualDate;
  private String patientUuid;

  public VisitDTOBuilder() {
    uuid = "123-456-789";
    startDate = new Date();
    location = new LocationBuilder().build().getName();
    type = new VisitTypeBuilder().build().getName();
    status = "SCHEDULED";
    formUri = "/some/uri";
    actualDate = null;
    patientUuid = new PatientBuilder().build().getUuid();
  }

  @Override
  public VisitDTO build() {
    return buildAsNew();
  }

  @Override
  public VisitDTO buildAsNew() {
    VisitDTO visitDTO = new VisitDTO();
    visitDTO.setUuid(uuid);
    visitDTO.setStartDate(startDate);
    visitDTO.setLocation(location);
    visitDTO.setType(type);
    visitDTO.setStatus(status);
    visitDTO.setFormUri(formUri);
    visitDTO.setActualDate(actualDate);
    visitDTO.setPatientUuid(patientUuid);
    return visitDTO;
  }

  public VisitDTOBuilder withUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public VisitDTOBuilder withStartDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  public VisitDTOBuilder withLocation(String location) {
    this.location = location;
    return this;
  }

  public VisitDTOBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public VisitDTOBuilder withStatus(String status) {
    this.status = status;
    return this;
  }

  public VisitDTOBuilder withFormUri(String formUri) {
    this.formUri = formUri;
    return this;
  }

  public VisitDTOBuilder withActualDate(Date actualDate) {
    this.actualDate = actualDate;
    return this;
  }

  public VisitDTOBuilder withPatientUuid(String patientUuid) {
    this.patientUuid = patientUuid;
    return this;
  }
}
