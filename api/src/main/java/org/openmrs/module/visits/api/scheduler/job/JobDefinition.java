package org.openmrs.module.visits.api.scheduler.job;

import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.HashMap;
import java.util.Map;

public abstract class JobDefinition extends AbstractTask {

  public abstract boolean shouldStartAtFirstCreation();

  public abstract String getTaskName();

  public abstract Class<? extends JobDefinition> getTaskClass();

  public Map<String, String> getProperties() {
    return new HashMap<>();
  }
}
