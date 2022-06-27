/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.util;

public final class VisitsConstants {

    public static final String DEFAULT_SERVER_SIDE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The datetime format which is used on server side.
     *
     * Note: To be careful with changing it. Date parsed to this format are directly passed to queries.
     * DB engines are sensitive to the precision of dates. Eg MySQL's functions are mainly based on the precision on
     * the seconds level (the function DATE). To be sure that comparing dates on the DB side works correctly we must use
     * consistent format on the backend server side.
     */
    public static final String DEFAULT_SERVER_SIDE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_FRONT_END_DATE_FORMAT = "dd MMM yyyy";

    public static final String CREATE_URI_NAME = "create";
    public static final String EDIT_URI_NAME = "edit";

    public static final String VISIT_CONFIG_SERVICE_BEAN_NAME = "visits.configService";

    private VisitsConstants() {
    }
}
