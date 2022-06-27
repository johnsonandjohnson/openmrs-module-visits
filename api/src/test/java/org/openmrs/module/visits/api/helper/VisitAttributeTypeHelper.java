/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.helper;

import org.openmrs.VisitAttributeType;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;

public final class VisitAttributeTypeHelper {

    public static VisitAttributeType createVisitAttributeType(String attrTypeName, String uuid) {
        VisitAttributeType visitAttributeType = new VisitAttributeType();
        visitAttributeType.setName(attrTypeName);
        visitAttributeType.setDatatypeClassname(FreeTextDatatype.class.getName());
        visitAttributeType.setUuid(uuid);

        return visitAttributeType;
    }

    private VisitAttributeTypeHelper() {
    }
}
