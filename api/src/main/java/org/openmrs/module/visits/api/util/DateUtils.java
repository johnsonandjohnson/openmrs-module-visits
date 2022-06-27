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

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    public static Date now() {
        return new Date();
    }

    public static Date today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean isFuture(Date date) {
        return date.after(now());
    }

    public static boolean isPast(Date date) {
        return !isFuture(date);
    }

    public static boolean isTodayOrAfter(Date date) {
        return !isBeforeToday(date);
    }

    public static boolean isBeforeToday(Date date) {
        return date.before(today());
    }

    private DateUtils() { }
}
