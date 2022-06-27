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

import org.openmrs.Patient;
import org.openmrs.module.visits.domain.criteria.VisitCriteria;

public class VisitCriteriaBuilder extends AbstractBuilder<VisitCriteria> {

    private static final int PATIENT_ID = 2512;
    private Integer patientId;

    public VisitCriteriaBuilder() {
        this.patientId = PATIENT_ID;
    }

    @Override
    public VisitCriteria build() {
        Patient patient = new PatientBuilder().withId(this.patientId).build();
        VisitCriteria criteria = new VisitCriteria(patient);
        return criteria;
    }

    @Override
    public VisitCriteria buildAsNew() {
        return withPatientId(null).build();
    }

    public VisitCriteriaBuilder withPatientId(Integer patientId) {
        this.patientId = patientId;
        return this;
    }
}
