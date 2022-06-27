/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.util;

import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.util.Comparator;

/**
 * Util class supporting with the visit domain comparators
 */
public final class ComparatorsHelper {

    /**
     * Method generating a visit domain wrapper comparator which compares by start date ascending
     *
     * @return an ascending start date comparator
     */
    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateAsc() {
      return Comparator.comparing(VisitDomainWrapper::getStartDate);
    }

    /**
     * Method generating a visit domain wrapper comparator which compares by start date descending
     *
     * @return a descending start date comparator
     */
    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateDesc() {
       return (v1, v2) -> (-1) * v1.getStartDate().compareTo(v2.getStartDate());
    }

    private ComparatorsHelper() { }
}
