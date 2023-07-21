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

/**
 * Class containing name of visit status actions/properties which are assigned to particular visit
 * status group.
 * E.g. you can add 'occurredVisitStatus' action to any of visit statuses that you want to treat as OCCURRED.
 */
public final class VisitStatusActionConstants {

  public static final String ENDING_VISIT = "endingVisit";

  public static final String MISSED_VISIT_STATUS = "missedVisitStatus";

  public static final String OCCURRED_VISIT_STATUS = "occurredVisitStatus";
}
