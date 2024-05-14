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

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.dto.LocationAttributeDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LocationsAttributesUtil {

  public static List<LocationAttributeDTO> getLocationsAttributes() {
    List<LocationAttributeDTO> locationAttributeDTOS = new ArrayList<>();
    List<Location> allLocations = Context.getLocationService().getAllLocations(false);
    for (Location location : allLocations) {
      List<LocationAttribute> locationAttributes = new ArrayList<>(location.getActiveAttributes());
      Map<String, String> locationAttributesMap = new HashMap<>();
      for (LocationAttribute attribute : locationAttributes) {
        locationAttributesMap.put(
            attribute.getAttributeType().getUuid(), attribute.getValueReference());
      }
      locationAttributeDTOS.add(
          new LocationAttributeDTO(location.getUuid(), locationAttributesMap));
    }

    return locationAttributeDTOS;
  }

  private LocationsAttributesUtil() {}
}
