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

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.VisitResource1_9;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Resource(order = VisitsRestConstants.VISITS_RESOURCE_ORDER, name = RestConstants.VERSION_1 + "/visit",
    supportedClass = Visit.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class VisitOverviewResource extends VisitResource1_9 {
  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    if (rep.getRepresentation().equals(VisitsRestConstants.OVERVIEW_VISIT_REPRESENTATION)) {
      final DelegatingResourceDescription overviewResourceDescription = new DelegatingResourceDescription();
      addVisitProperties(overviewResourceDescription);
      addGeneratedProperties(overviewResourceDescription);
      addAttributeProperties(overviewResourceDescription);
      return overviewResourceDescription;
    }

    return super.getRepresentationDescription(rep);
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

  private void addGeneratedProperties(DelegatingResourceDescription description) {
    description.addProperty("actualDate");
    description.addProperty("formUri");
  }

  @Override
  public Object getProperty(Visit instance, String propertyName) throws ConversionException {
    if (isDynamicProperty(propertyName)) {
      final List<Object> attributeProperties = instance
          .getActiveAttributes()
          .stream()
          .filter(visitAttribute -> propertyName.equals(visitAttribute.getAttributeType().getName()))
          .map(BaseAttribute::getValue)
          .collect(Collectors.toList());

      if (attributeProperties.isEmpty()) {
        return null;
      } else if (attributeProperties.size() == 1) {
        return attributeProperties.get(0);
      } else {
        return attributeProperties;
      }
    }

    return super.getProperty(instance, propertyName);
  }

  /**
   * Checks if Visit's property with name {@code propertyName} is a dynamic property of a Visit. Dynamic property is a
   * property which is not Visit Java Beans property and not a property for which a @PropertyGetter annotated method exists.
   *
   * @param propertyName, the name of property to check
   * @return true if propertyName is a dynamic property
   */
  private boolean isDynamicProperty(String propertyName) {
    final DelegatingResourceDescription overviewResourceDescription = new DelegatingResourceDescription();
    addVisitProperties(overviewResourceDescription);
    addGeneratedProperties(overviewResourceDescription);
    return !overviewResourceDescription.getProperties().containsKey(propertyName);
  }

  private VisitAttributeType mergeWithException(VisitAttributeType visitAttributeType1,
                                                VisitAttributeType visitAttributeType2) {
    throw new APIException(MessageFormat.format("Duplicated name for Visit Attribute Type {0}, duplicated uuid: {1}, {2}",
        visitAttributeType1.getName(), visitAttributeType1.getUuid(), visitAttributeType2.getUuid()));
  }

  @Override
  @PropertyGetter("display")
  public String getDisplayString(Visit visit) {
    return super.getDisplayString(visit);
  }

  @PropertyGetter("actualDate")
  public Date getActualDate(Visit visit) {
    Set<Encounter> visitEncounters = visit.getEncounters();
    if (visitEncounters == null) {
      return null;
    }

    // Return the encounter's Datetime of the latest non-voided Encounter
    return visitEncounters
        .stream()
        .filter(encounter -> !encounter.getVoided())
        .map(Encounter::getEncounterDatetime)
        .max(Comparator.naturalOrder())
        .orElse(null);
  }

  @PropertyGetter("formUri")
  public String getFormUri(Visit visit) {
    return Context.getService(ConfigService.class).getVisitFormUrisMap().getUri(visit);
  }
}
