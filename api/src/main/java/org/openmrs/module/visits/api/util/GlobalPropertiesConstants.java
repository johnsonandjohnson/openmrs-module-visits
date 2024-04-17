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

public final class GlobalPropertiesConstants {

  public static final GPDefinition PAST_VISITS_LIMIT =
      new GPDefinition(
          "visits.past-visits-limit",
          "1",
          "Limit of past visits displayed on the patient dashboard visits widget.",
          true);

  public static final GPDefinition UPCOMING_VISITS_LIMIT =
      new GPDefinition(
          "visits.upcoming-visits-limit",
          "2",
          "Limit of upcoming visits displayed on the patient dashboard visits widget.",
          true);

  public static final String VISIT_FORM_URIS_KEY = "visits.visit-form-uris";

  public static final GPDefinition MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED =
      new GPDefinition(
          "visits.minimumVisitDelayToAutomaticallyMarkItAsMissed",
          "1",
          "The number of days after which "
              + "missed visits should be automatically marked as missed.",
          true);

  public static final GPDefinition ENCOUNTER_DATETIME_VALIDATION =
      new GPDefinition(
          "visits.encounterDatetimeValidation",
          "true",
          "Used to control if encounter datetime should be validated. By default OpenMRS doesn't support adding "
              + "encounter if its date isn't between visit start and end date. This GP can be used to turn off this "
              + "default validation. When the value is set to false then the custom EnterHtmlFormFragment is used to "
              + "override the encounter validation in emr api and additionally OpenMRS core encounter validator "
              + "is overridden. Possible values: true, false",
          true);

  public static final GPDefinition MISSED_VISIT_CHANGER_CREATION_GP =
      new GPDefinition(
          "visits.isMissedVisitChangerShouldBeCreated",
          "false",
          "Used to determine if Missed Visits Status Changer task should be created during module startup. "
              + "Changes will be applied the next time the module is restarted. If task already exists "
              + "and GP value is false it will not remove task during next server restart - it has to be "
              + "removed manually. Possible values: true, false");

  public static final GPDefinition SCHEDULE_VISIT_EXTRA_INFORMATION_GP =
      new GPDefinition(
          "visits.extraSchedulingInformationEnabled",
          "false",
          "Used to determine if additional information(e.g. warning that visit will be scheduled on one of holiday day)"
              + " should be visible when scheduling a visit.");

  public static final GPDefinition VISITS_HOLIDAY_WEEKDAYS_GP =
      new GPDefinition(
          "visits.holidayWeekdays",
          "Saturday,Sunday",
          "Comma separated list of weekdays when visits should not be scheduled");

  public static final GPDefinition SCHEDULE_VISIT_OUTSIDE_DATE_WINDOW_EXTRA_INFORMATION_GP =
      new GPDefinition(
          "visits.outsideDateWindowInformationEnabled",
          "true",
          "Used to determine if additional information about falling outside the permitted visit date should be displayed");

  public static final GPDefinition VISIT_TYPES_TIME_WINDOW =
      new GPDefinition(
          "visits.visitTypesWithTimeWindow",
          "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
          "Comma separated list of visit type uuids used to determine which should have time window defined.");

  private GlobalPropertiesConstants() {}
}
