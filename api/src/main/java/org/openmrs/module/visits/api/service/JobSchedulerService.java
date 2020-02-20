package org.openmrs.module.visits.api.service;

import java.util.Date;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.visits.api.scheduler.job.JobDefinition;
import org.openmrs.module.visits.api.scheduler.job.JobRepeatInterval;

public interface JobSchedulerService extends OpenmrsService {

    void rescheduleOrCreateNewTask(JobDefinition jobDefinition, JobRepeatInterval repeatInterval);

    void createNewTask(JobDefinition jobDefinition, Date startTime,
                       JobRepeatInterval repeatInterval);

    void setDaemonToken(DaemonToken daemonToken);
}
