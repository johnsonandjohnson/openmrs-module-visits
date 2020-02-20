package org.openmrs.module.visits.api.scheduler.job;

import org.openmrs.module.visits.api.util.DateUtil;

public enum JobRepeatInterval {
    DAILY(DateUtil.DAY_IN_SECONDS),
    HOURLY(DateUtil.HOUR_IN_SECONDS),
    NEVER(0);

    JobRepeatInterval(long seconds) {
        this.seconds = seconds;
    }

    private long seconds;

    public long getSeconds() {
        return seconds;
    }
}
