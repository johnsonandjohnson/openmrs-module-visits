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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class VisitOverviewResourceTest {

  @Mock
  private VisitService visitService;

  @Before
  public void setUp() {
    mockStatic(Context.class);

    when(Context.getVisitService()).thenReturn(visitService);
    when(visitService.getAllVisitAttributeTypes()).thenReturn(
        Arrays.asList(
            createVisitAttributeType("Visit Status"),
            createVisitAttributeType("Visit Time")
        )
    );
  }

  @Test
  public void shouldAddPropertiesForVisitsOverview() {
    DelegatingResourceDescription actual = new VisitOverviewResource()
        .getRepresentationDescription(createNamedRepresentation(VisitsRestConstants.OVERVIEW_VISIT_REPRESENTATION));

    assertTrue(actual.getProperties().containsKey("uuid"));
    assertTrue(actual.getProperties().containsKey("display"));
    assertTrue(actual.getProperties().containsKey("patient"));
    assertTrue(actual.getProperties().containsKey("visitType"));
    assertTrue(actual.getProperties().containsKey("location"));
    assertTrue(actual.getProperties().containsKey("startDatetime"));
    assertTrue(actual.getProperties().containsKey("stopDatetime"));
    assertTrue(actual.getProperties().containsKey("voided"));
    assertTrue(actual.getProperties().containsKey("actualDate"));
    assertTrue(actual.getProperties().containsKey("formUri"));
    assertTrue(actual.getProperties().containsKey("Visit Status"));
    assertTrue(actual.getProperties().containsKey("Visit Time"));
  }

  @Test
  public void shouldReturnNullWhenNotRecognizedRepresentationName() {
    DelegatingResourceDescription actual = new VisitOverviewResource()
        .getRepresentationDescription(createNamedRepresentation("non-overview-representation"));

    assertNull(actual);
  }

  private Representation createNamedRepresentation(String representation) {
    return new NamedRepresentation(representation);
  }

  private VisitAttributeType createVisitAttributeType(String name) {
    VisitAttributeType type = new VisitAttributeType();
    type.setName(name);
    type.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
    type.setRetired(false);
    return type;
  }
}