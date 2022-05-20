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

import org.openmrs.VisitType;

public class VisitTypeBuilder extends AbstractBuilder<VisitType> {

    private Integer id;
    private String name;
    private String description;

    public VisitTypeBuilder() {
        name = "Visit type name";
        description = "Visit type description";
    }

    public VisitType build() {
        VisitType visitType = buildAsNew();
        visitType.setId(id == null ? getAndIncrementNumber() : id);
        return visitType;
    }

    @Override
    public VisitType buildAsNew() {
        return new VisitType(name, description);
    }

    public VisitTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public VisitTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VisitTypeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
}
