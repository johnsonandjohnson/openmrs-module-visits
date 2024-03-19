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

import java.util.Map;

/** Object representing a visit DTO extended with the additional properties */
public class VisitDetailsDTO extends VisitDTO {

  private static final long serialVersionUID = 15L;

  private String locationName;
  private String typeName;
  private Map<String, String> visitAttributes;

  public VisitDetailsDTO() {}

  /**
   * Constructor of the visit details DTO
   *
   * @param visitDTO base visit DTO object
   * @param locationName name of the location
   * @param typeName name of the type
   */
  public VisitDetailsDTO(
      VisitDTO visitDTO,
      String locationName,
      String typeName,
      Map<String, String> visitAttributes) {
    super(
        visitDTO.getUuid(),
        visitDTO.getLocation(),
        visitDTO.getType(),
        visitDTO.getStatus(),
        visitDTO.getFormUri(),
        visitDTO.getPatientUuid(),
        visitDTO.getVisitDateDTO());
    this.locationName = locationName;
    this.typeName = typeName;
    this.visitAttributes = visitAttributes;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public Map<String, String> getVisitAttributes() {
    return visitAttributes;
  }

  public void setVisitAttributes(Map<String, String> visitAttributes) {
    this.visitAttributes = visitAttributes;
  }
}
