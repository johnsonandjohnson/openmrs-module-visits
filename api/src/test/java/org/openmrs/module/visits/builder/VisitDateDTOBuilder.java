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

import org.openmrs.module.visits.api.dto.VisitDateDTO;

import java.util.Date;

public class VisitDateDTOBuilder extends AbstractBuilder<VisitDateDTO> {

    private Date startDate;

    private String time;

    private Date actualDate;

    @Override
    public VisitDateDTO build() {
        return buildAsNew();
    }

    @Override
    public VisitDateDTO buildAsNew() {
        VisitDateDTO visitDateDTO = new VisitDateDTO();
        visitDateDTO.setStartDate(startDate);
        visitDateDTO.setTime(time);
        visitDateDTO.setActualDate(actualDate);

        return visitDateDTO;
    }

    public VisitDateDTOBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public VisitDateDTOBuilder withTime(String time) {
        this.time = time;
        return this;
    }

    public VisitDateDTOBuilder withActualDate(Date actualDate) {
        this.actualDate = actualDate;
        return this;
    }
}
