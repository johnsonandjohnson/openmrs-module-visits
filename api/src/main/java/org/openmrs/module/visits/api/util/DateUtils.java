package org.openmrs.module.visits.api.util;

import java.util.Date;

public final class DateUtils {

    public static boolean isFuture(Date date) {
        return date.after(new Date());
    }

    public static boolean isPast(Date date) {
        return !isFuture(date);
    }

    private DateUtils() { }
}
