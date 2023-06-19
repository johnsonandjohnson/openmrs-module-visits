/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.rest.web.service.impl;

import org.openmrs.VisitAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.visits.rest.web.service.VisitOverviewResourceService;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VisitOverviewResourceServiceImpl implements VisitOverviewResourceService {
  private DelegatingResourceDescription overviewRepresentationDescription;

  @Override
  public synchronized DelegatingResourceDescription getOverviewRepresentation() {
    if (overviewRepresentationDescription == null) {
      overviewRepresentationDescription = getNewOverviewRepresentation();
    }

    return overviewRepresentationDescription;
  }

  @Override
  public boolean isDynamicProperty(String propertyName) {
    final DelegatingResourceDescription overviewResourceDescription = new DelegatingResourceDescription();
    addVisitProperties(overviewResourceDescription);
    addGeneratedProperties(overviewResourceDescription);
    return !overviewResourceDescription.getProperties().containsKey(propertyName);
  }

  @Override
  public synchronized void invalidateCache() {
    overviewRepresentationDescription = null;
  }

  private DelegatingResourceDescription getNewOverviewRepresentation() {
    final DelegatingResourceDescription overviewResourceDescription = new DelegatingResourceDescription();
    addVisitProperties(overviewResourceDescription);
    addGeneratedProperties(overviewResourceDescription);
    addAttributeProperties(overviewResourceDescription);
    return overviewResourceDescription;
  }

  private void addVisitProperties(DelegatingResourceDescription description) {
    description.addProperty("uuid");
    description.addProperty("display");
    description.addProperty("patient", new NamedRepresentation(VisitsRestConstants.RESOLVED_PATIENT_REPRESENTATION));
    description.addProperty("visitType", Representation.REF);
    description.addProperty("location", Representation.REF);
    description.addProperty("startDatetime");
    description.addProperty("stopDatetime");
    description.addProperty("voided");
    description.addSelfLink();
  }

  private void addGeneratedProperties(DelegatingResourceDescription description) {
    description.addProperty("actualDate");
    description.addProperty("formUri");
  }

  private void addAttributeProperties(DelegatingResourceDescription description) {
    getVisitAttributeTypeMap().values().forEach(type -> addAttributeProperty(description, type));
  }

  private Map<String, VisitAttributeType> getVisitAttributeTypeMap() {
    return Context
        .getVisitService()
        .getAllVisitAttributeTypes()
        .stream()
        .filter(type -> !type.getRetired())
        .collect(Collectors.toMap(VisitAttributeType::getName, Function.identity(), this::mergeWithException));
  }

  private void addAttributeProperty(DelegatingResourceDescription description, VisitAttributeType visitAttributeType) {
    try {
      description.addProperty(visitAttributeType.getName(), Representation.FULL,
          Class.forName(visitAttributeType.getDatatypeClassname()));
    } catch (ClassNotFoundException e) {
      throw new APIException(
          MessageFormat.format("Could not find class for visit attribute: {0} and dataTypeClassname: " + "{1}",
              visitAttributeType.getName(), visitAttributeType.getDatatypeClassname()), e);
    }
  }

  private VisitAttributeType mergeWithException(VisitAttributeType visitAttributeType1,
                                                VisitAttributeType visitAttributeType2) {
    throw new APIException(MessageFormat.format("Duplicated name for Visit Attribute Type {0}, duplicated uuid: {1}, {2}",
        visitAttributeType1.getName(), visitAttributeType1.getUuid(), visitAttributeType2.getUuid()));
  }

}
