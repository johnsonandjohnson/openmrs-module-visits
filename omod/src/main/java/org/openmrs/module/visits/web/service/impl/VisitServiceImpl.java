/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.web.service.VisitService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitServiceImpl implements VisitService {

    @Override
    public List<String> getVisitTimes() {
        String visitTimesProperty = Context.getAdministrationService()
                .getGlobalProperty(ConfigConstants.VISIT_TIMES_KEY);
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(visitTimesProperty)) {
            results.addAll(Arrays.asList(visitTimesProperty.split(ConfigConstants.COMMA_SEPARATOR)));
        }
        return results;
    }

    @Override
    public List<String> getVisitStatuses() {
        String visitTimesProperty = Context.getAdministrationService()
                .getGlobalProperty(ConfigConstants.VISIT_STATUSES_KEY);
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(visitTimesProperty)) {
            results.addAll(Arrays.asList(visitTimesProperty.split(ConfigConstants.COMMA_SEPARATOR)));
        }
        return results;
    }
}
