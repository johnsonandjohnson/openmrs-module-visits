/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.builder;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;

public class VisitAttributeBuilder extends AbstractBuilder<VisitAttribute> {
    private Integer visitAttributeId;
    private VisitAttributeType visitAttributeType;

    public VisitAttributeBuilder() {
        super();
        visitAttributeType = new VisitAttributeType();
    }

    @Override
    public VisitAttribute build() {
        return buildAsNew();
    }

    @Override
    public VisitAttribute buildAsNew() {
        VisitAttribute va = new VisitAttribute();
        va.setVisitAttributeId(visitAttributeId == null ? getAndIncrementNumber() : visitAttributeId);
        va.setAttributeType(visitAttributeType);
        va.setValueReferenceInternal("test value");
        return va;
    }
}
