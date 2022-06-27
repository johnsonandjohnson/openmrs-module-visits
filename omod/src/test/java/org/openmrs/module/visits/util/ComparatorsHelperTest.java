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

import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ComparatorsHelperTest {

    private static final String JANUARY_FIRST = "2020-01-01";
    private static final String JANUARY_SECOND = "2020-01-02";
    private static final String FEBRUARY_FIRST = "2020-02-01";

    @Test
    public void shouldSortVisitsByStartDateInAscendingOrder() {
        List<VisitDomainWrapper> list = Arrays.asList(
                getVisitWrapper(JANUARY_FIRST),
                getVisitWrapper(FEBRUARY_FIRST),
                getVisitWrapper(JANUARY_SECOND));

        list.sort(ComparatorsHelper.getVisitsComparatorByStartDateAsc());
        assertTrue(list.get(0).getStartDate().before(list.get(1).getStartDate()));
        assertTrue(list.get(1).getStartDate().before(list.get(2).getStartDate()));
    }

    @Test
    public void shouldSortVisitsByStartDateInDescendingOrder() {
        List<VisitDomainWrapper> list = Arrays.asList(
                getVisitWrapper(JANUARY_FIRST),
                getVisitWrapper(FEBRUARY_FIRST),
                getVisitWrapper(JANUARY_SECOND));

        list.sort(ComparatorsHelper.getVisitsComparatorByStartDateDesc());
        assertTrue(list.get(0).getStartDate().after(list.get(1).getStartDate()));
        assertTrue(list.get(1).getStartDate().after(list.get(2).getStartDate()));
    }

    private VisitDomainWrapper getVisitWrapper(String startDate) {
        Visit visit = new Visit();
        visit.setStartDatetime(Date.valueOf(startDate));
        VisitDomainWrapper result = new VisitDomainWrapper();
        result.setVisit(visit);
        return result;
    }
}
