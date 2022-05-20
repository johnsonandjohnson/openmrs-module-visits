/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.scheduler.job;

import org.openmrs.module.visits.api.util.DateUtil;

/**
 * Enum representing an interval between job execution
 */
public enum JobRepeatInterval {
    DAILY(DateUtil.DAY_IN_SECONDS),
    HOURLY(DateUtil.HOUR_IN_SECONDS),
    NEVER(0);

    /**
     * Job repeat interval enum constructor
     *
     * @param seconds job execution interval expressed in seconds
     */
    JobRepeatInterval(long seconds) {
        this.seconds = seconds;
    }

    private long seconds;

    /**
     * @return job execution interval expressed in seconds
     */
    public long getSeconds() {
        return seconds;
    }
}
