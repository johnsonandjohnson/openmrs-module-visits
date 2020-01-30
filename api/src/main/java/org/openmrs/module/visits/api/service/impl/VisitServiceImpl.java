/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.service.VisitService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.OverviewCriteria;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;

public class VisitServiceImpl extends BaseOpenmrsDataService<Visit> implements VisitService {

    private PatientService patientService;

    private LocationService locationService;

    private VisitMapper visitMapper;

    @Override
    public List<String> getVisitTimes() {
        String visitTimesProperty = Context.getAdministrationService()
                .getGlobalProperty(ConfigConstants.VISIT_TIMES_KEY);
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(visitTimesProperty)) {
            results.addAll(Arrays.asList(visitTimesProperty.split(ConfigConstants.COMMA_SEPARATOR)));
        }
        return results;
    }

    @Override
    public List<String> getVisitStatuses() {
        String visitTimesProperty = Context.getAdministrationService()
                .getGlobalProperty(ConfigConstants.VISIT_STATUSES_KEY);
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(visitTimesProperty)) {
            results.addAll(Arrays.asList(visitTimesProperty.split(ConfigConstants.COMMA_SEPARATOR)));
        }
        return results;
    }

    @Override
    public List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        if (patient == null) {
            throw new ValidationException(String.format("Patient with uuid %s does not exist", patientUuid));
        }
        return findAllByCriteria(new VisitCriteria(patient), pagingInfo);
    }

    @Override
    public void updateVisit(String visitUuid, VisitDTO visitDTO) {
        saveOrUpdate(visitMapper.fromDto(visitDTO.setUuid(visitUuid)));
    }

    public void setVisitMapper(VisitMapper visitMapper) {
        this.visitMapper = visitMapper;
    }

    @Override
    public List<Visit> getVisitsForLocation(String locationUuid, PagingInfo pagingInfo) {
        Location location = locationService.getLocationByUuid(locationUuid);
        if (location == null) {
            throw new ValidationException(String.format("Location with uuid %s does not exist",
                    locationUuid));
        }
        return findAllByCriteria(new OverviewCriteria(location), pagingInfo);
    }

    public VisitServiceImpl setPatientService(PatientService patientService) {
        this.patientService = patientService;
        return this;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
