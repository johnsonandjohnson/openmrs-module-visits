package org.openmrs.module.visits.rest.web;

public class VisitsRestConstants {
  /**
   * The order value of any OpenMRS REST resource added by Visits module.
   */
  public static final int VISITS_RESOURCE_ORDER = 10;
  /**
   * The name of OpenMRS REST representation for Visit resource which is tailored for CfL overview.
   */
  public static final String OVERVIEW_VISIT_REPRESENTATION = "overview";
  /**
   * The name of OpenMRS REST representation for Patient resource where identifiers values are resolved as fields.
   */
  public static final String RESOLVED_PATIENT_REPRESENTATION = "resolved";

  private VisitsRestConstants() {
  }
}
