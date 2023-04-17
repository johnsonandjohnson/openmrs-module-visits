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

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.service.PatientResolvedResourceService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PatientResolvedResourceServiceImpl implements PatientResolvedResourceService {
  private DelegatingResourceDescription resolvedRepresentationDescription;

  @Override
  public synchronized DelegatingResourceDescription getResolvedRepresentation() {
    if (resolvedRepresentationDescription == null) {
      resolvedRepresentationDescription = getNewResolvedRepresentation();
    }

    return resolvedRepresentationDescription;
  }

  @Override
  public boolean isDynamicProperty(String propertyName) {
    final DelegatingResourceDescription description = new DelegatingResourceDescription();
    addPatientProperties(description);
    return !description.getProperties().containsKey(propertyName);
  }

  @Override
  public synchronized void invalidateCache() {
    resolvedRepresentationDescription = null;
  }

  private DelegatingResourceDescription getNewResolvedRepresentation() {
    final DelegatingResourceDescription description = new DelegatingResourceDescription();
    addPatientProperties(description);
    addIdentifierProperties(description);
    return description;
  }

  private void addPatientProperties(DelegatingResourceDescription description) {
    description.addProperty("uuid");
    description.addProperty("display");
    description.addProperty("identifiers", Representation.REF);
    description.addProperty("person", Representation.DEFAULT);
    description.addProperty("voided");
    description.addSelfLink();
    description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
  }

  private void addIdentifierProperties(DelegatingResourceDescription description) {
    getPatientIdentifierTypeMap().values().forEach(type -> addIdentifierTypeProperty(description, type));
  }

  private Map<String, PatientIdentifierType> getPatientIdentifierTypeMap() {
    return Context
        .getPatientService()
        .getAllPatientIdentifierTypes()
        .stream()
        .collect(Collectors.toMap(PatientIdentifierType::getName, Function.identity(), this::mergeWithException));
  }

  private void addIdentifierTypeProperty(DelegatingResourceDescription description,
                                         PatientIdentifierType patientIdentifierType) {
    description.addProperty(patientIdentifierType.getName());
  }

  private PatientIdentifierType mergeWithException(PatientIdentifierType patientIdentifierType1,
                                                   PatientIdentifierType patientIdentifierType2) {
    throw new APIException(MessageFormat.format("Duplicated name for Patient Identifier Type {0}, duplicated uuid: {1}, {2}",
        patientIdentifierType1.getName(), patientIdentifierType1.getUuid(), patientIdentifierType2.getUuid()));
  }

}
