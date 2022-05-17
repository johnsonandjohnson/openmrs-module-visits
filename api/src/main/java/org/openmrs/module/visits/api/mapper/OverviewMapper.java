/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang.NotImplementedException;
import org.openmrs.Visit;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.NameUrlDTO;
import org.openmrs.module.visits.api.dto.OverviewDTO;

/**
 * Maps objects between the related visit overview types
 */
public final class OverviewMapper extends AbstractMapper<OverviewDTO, Visit> {
    private static final String URL = "/openmrs/coreapps/clinicianfacing/patient.page?patientId=";

    @Override
    public OverviewDTO toDto(Visit visit) {
        VisitDecorator visitDecorator = new VisitDecorator(visit);
        NameUrlDTO name = new NameUrlDTO(visit.getPatient().getPersonName().getFullName(),
                URL + visit.getPatient().getUuid());
        return new OverviewDTO()
                .setUuid(visit.getUuid())
                .setPatientIdentifier(visit.getPatient().getPatientIdentifier().getIdentifier())
                .setNameUrl(name)
                .setStartDate(visit.getStartDatetime())
                .setActualDate(visitDecorator.getActualDate())
                .setTime(visitDecorator.getTime())
                .setType(visit.getVisitType() == null ? null : visit.getVisitType().getName())
                .setStatus(visitDecorator.getStatus())
                .setLocation(visit.getLocation() == null ? null : visit.getLocation().getName());
    }

    @Override
    public Visit fromDto(OverviewDTO dto) {
        throw new NotImplementedException("Mapping from dto not supported");
    }
}
