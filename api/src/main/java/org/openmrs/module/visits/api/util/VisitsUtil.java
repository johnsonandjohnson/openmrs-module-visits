/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.util;

import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VisitsUtil {

  public static Map<String, String> createVisitAttributesMap(Visit visit) {
    List<VisitAttribute> visitAttributes = new ArrayList<>(visit.getActiveAttributes());
    Map<String, String> visitAttributesMap = new HashMap<>();
    for (VisitAttribute attribute : visitAttributes) {
      visitAttributesMap.put(attribute.getAttributeType().getName(), attribute.getValueReference());
    }

    return visitAttributesMap;
  }

  private VisitsUtil() {}
}
