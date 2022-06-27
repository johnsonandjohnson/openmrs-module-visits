/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.visits.api.scheduler.job.JobDefinition;
import org.openmrs.module.visits.api.scheduler.job.JobRepeatInterval;

import java.util.Date;

/**
 * Serves with a functionality related to the job scheduling management
 */
public interface JobSchedulerService extends OpenmrsService {

    /**
     * Reschedules a task if already exists, otherwise creates a new scheduled task.
     * Start date is set to now.
     *
     * @param jobDefinition object containing the important data about job
     * @param repeatInterval interval between job executions
     */
    void rescheduleOrCreateNewTask(JobDefinition jobDefinition, JobRepeatInterval repeatInterval);

    /**
     * Creates a new scheduled task
     *
     * @param jobDefinition object containing the important data about job
     * @param startTime date when the scheduled task will start from
     * @param repeatInterval interval between job executions
     */
    void createNewTask(JobDefinition jobDefinition, Date startTime,
                       JobRepeatInterval repeatInterval);

    /**
     * Setting up a token for daemon execution
     *
     * @param daemonToken daemon token
     */
    void setDaemonToken(DaemonToken daemonToken);
}
