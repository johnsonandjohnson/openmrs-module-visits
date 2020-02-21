package org.openmrs.module.visits.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtil;

import java.util.List;

/**
 * Provides the default implementation of module configuration set
 */
public class ConfigServiceImpl implements ConfigService {

    private static final String COMMA_DELIMITER = ",";

    @Override
    public int getMinimumVisitDelayToMarkItAsMissed() {
        String gpName = ConfigConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED_KEY;
        return GlobalPropertyUtil.parseInt(gpName, getGp(gpName));
    }

    @Override
    public List<String> getStatusesEndingVisit() {
        return GlobalPropertyUtil.parseList(
                getGp(ConfigConstants.STATUSES_ENDING_VISIT_KEY),
                COMMA_DELIMITER);
    }

    @Override
    public String getStatusOfMissedVisit() {
        return getGp(ConfigConstants.STATUS_OF_MISSED_VISIT_KEY);
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }
}
