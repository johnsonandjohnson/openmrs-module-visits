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
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.visits.rest.web.service.VisitOverviewResourceService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.VisitResource1_9;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Resource(order = VisitsRestConstants.VISITS_RESOURCE_ORDER, name = RestConstants.VERSION_1 + "/visit",
    supportedClass = Visit.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class VisitOverviewResource extends VisitResource1_9 {
  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    if (rep.getRepresentation().equals(VisitsRestConstants.OVERVIEW_VISIT_REPRESENTATION)) {
      return Context.getService(VisitOverviewResourceService.class).getOverviewRepresentation();
    }

    return super.getRepresentationDescription(rep);
  }

  @Override
  public Object getProperty(Visit instance, String propertyName) throws ConversionException {
    if (Context.getService(VisitOverviewResourceService.class).isDynamicProperty(propertyName)) {
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
