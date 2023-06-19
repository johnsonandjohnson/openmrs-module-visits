/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.rest.web.service;

import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * @implNote The default implementation does cache Patient Identifier Types, but doesn't observe changes. It's required to
 * call {@link #invalidateCache()} after any change to Patient Identifiers Types to let this service rebuild its cache.
 * @see org.openmrs.module.visits.rest.web.service.impl.aop.PatientIdentifierTypeOnUpdateAdvice
 */
public interface PatientResolvedResourceService {
  /**
   * @return a description of REST Representation for resolved (patient identifiers are serialized as direct JSON properties)
   * patient, never null
   */
  DelegatingResourceDescription getResolvedRepresentation();

  /**
   * Checks if Patient's property with name {@code propertyName} is a dynamic property of a Patient. Dynamic property is a
   * property which is not Patient Java Beans property and not a property for which a @PropertyGetter annotated method
   * exists.
   *
   * @param propertyName, the name of property to check
   * @return true if propertyName is a dynamic property
   */
  boolean isDynamicProperty(String propertyName);

  /**
   * Invalidates internal cache.
   */
  void invalidateCache();
}
