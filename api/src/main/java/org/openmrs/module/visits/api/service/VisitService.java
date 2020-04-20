/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import org.openmrs.Visit;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.domain.PagingInfo;

import java.util.List;

/**
 * Provides methods for creating, reading, updating and deleting Visit entities
 */
public interface VisitService extends BaseOpenmrsCriteriaDataService<Visit> {

    /**
     * Finds paginated collection of the visits for the given patient
     *
     * @param patientUuid uuid of the patient
     * @param pagingInfo properties of the pagination
     * @return list of the patient's visits, implicitly paginated
     */
    List<Visit> getVisitsForPatient(String patientUuid, PagingInfo pagingInfo);

    /**
     * Finds paginated collection of the visits for the given location, optionally filtered by a query
     *
     * @param locationUuid uuid of the location
     * @param pagingInfo properties of the pagination
     * @return list of the location's visits, implicitly paginated
     */
    List<Visit> getVisitsForLocation(String locationUuid, PagingInfo pagingInfo, String query);


    /**
     * @param visitUuid
     * @param visitDTO
     */
    void updateVisit(String visitUuid, VisitDTO visitDTO);

    void changeStatusForMissedVisits();

    void createVisit(VisitDTO visit);
}
