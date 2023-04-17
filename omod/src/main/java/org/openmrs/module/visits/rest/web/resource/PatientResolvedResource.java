/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.rest.web.resource;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.visits.rest.web.service.PatientResolvedResourceService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import java.util.List;
import java.util.stream.Collectors;

@Resource(order = VisitsRestConstants.VISITS_RESOURCE_ORDER, name = RestConstants.VERSION_1 + "/patient",
    supportedClass = Patient.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class PatientResolvedResource extends PatientResource1_8 {
  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    if (rep.getRepresentation().equals(VisitsRestConstants.RESOLVED_PATIENT_REPRESENTATION)) {
      return Context.getService(PatientResolvedResourceService.class).getResolvedRepresentation();
    }

    return super.getRepresentationDescription(rep);
  }

  @Override
  public Object getProperty(Patient instance, String propertyName) throws ConversionException {
    if (Context.getService(PatientResolvedResourceService.class).isDynamicProperty(propertyName)) {
      final List<String> identifierProperties = instance
          .getActiveIdentifiers()
          .stream()
          .filter(patientIdentifier -> propertyName.equals(patientIdentifier.getIdentifierType().getName()))
          .map(PatientIdentifier::getIdentifier)
          .collect(Collectors.toList());

      if (identifierProperties.isEmpty()) {
        return null;
      } else if (identifierProperties.size() == 1) {
        return identifierProperties.get(0);
      } else {
        return identifierProperties;
      }
    }

    return super.getProperty(instance, propertyName);
  }

  @Override
  @PropertyGetter("display")
  public String getDisplayString(Patient patient) {
    return patient.getPatientIdentifier() == null ? "" :
        patient.getPatientIdentifier().getIdentifier() + " - " + patient.getPersonName().getFullName();
  }
}
