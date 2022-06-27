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

import org.openmrs.Location;

public class LocationBuilder extends AbstractBuilder<Location> {

    private Integer id;
    private String name;

    public LocationBuilder() {
        super();
        name = "Some location" + id;
    }

    @Override
    public Location build() {
        Location location = buildAsNew();
        location.setId(id == null ? getAndIncrementNumber() : null);
        return location;
    }

    @Override
    public Location buildAsNew() {
        Location location = new Location(this.id);
        location.setName(this.name);
        return location;
    }

    public LocationBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
