package org.openmrs.module.visits.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MissedVisitsStatusChangerJobDefinition extends JobDefinition {

    private static final Log LOGGER = LogFactory.getLog(MissedVisitsStatusChangerJobDefinition.class);
    private static final String TASK_NAME = "Missed Visits Status Changer";

    @Override
    public void execute() {
        LOGGER.info(getTaskName() + " started");
        // TODO: CFLM-747 it will be implemented in the next PRs
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public boolean shouldStartAtFirstCreation() {
        return true;
    }

    @Override
    public Class getTaskClass() {
        return MissedVisitsStatusChangerJobDefinition.class;
    }
}
