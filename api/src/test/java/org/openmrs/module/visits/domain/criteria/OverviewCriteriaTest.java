/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.CriterionEntry;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.module.visits.builder.LocationBuilder;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class OverviewCriteriaTest {

    private static final String SAMPLE_QUERY = "SELECT 1";
    private Location location = new LocationBuilder().build();

    @Mock
    private SessionImplementor session;

    @Test
    public void shouldLoadHibernateCriteriaWithLocation() throws NoSuchFieldException, IllegalAccessException {
        OverviewCriteria overviewCriteria = new OverviewCriteria(location, SAMPLE_QUERY, null, null,
                null, null);
        Criteria criteria = new CriteriaImpl("Overview", session);
        overviewCriteria.loadHibernateCriteria(criteria);

        assertThat(getCriterions(criteria), hasSize(2));
    }

    @Test
    public void shouldBuildCriteriaWithLocationUuid() {
        OverviewCriteria overviewCriteria = OverviewCriteria.forLocationUuid(location.getUuid());
        assertEquals(location.getUuid(), overviewCriteria.getLocationUuid());
    }

    private List<CriterionEntry> getCriterions(Criteria criteria) throws NoSuchFieldException, IllegalAccessException {
        Field privateStringField = CriteriaImpl.class.
                getDeclaredField("criterionEntries");
        privateStringField.setAccessible(true);

        return (List<CriterionEntry>) privateStringField.get(criteria);
    }
}
