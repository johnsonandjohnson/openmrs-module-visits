package org.openmrs.module.visits.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.GlobalPropertiesConstants;
import org.openmrs.module.visits.api.util.GlobalPropertyUtil;

import java.util.List;

/**
 * Provides the default implementation of module configuration set
 */
public class ConfigServiceImpl implements ConfigService {

    private static final String COMMA_DELIMITER = ",";

    @Override
    public int getMinimumVisitDelayToMarkItAsMissed() {
        String gpName = GlobalPropertiesConstants.MINIMUM_VISIT_DELAY_TO_MARK_IT_AS_MISSED.getKey();
        return GlobalPropertyUtil.parseInt(gpName, getGp(gpName));
    }

    @Override
    public List<String> getStatusesEndingVisit() {
        return GlobalPropertyUtil.parseList(
                getGp(GlobalPropertiesConstants.STATUSES_ENDING_VISIT.getKey()),
                COMMA_DELIMITER);
    }

    @Override
    public String getStatusOfMissedVisit() {
        return getGp(GlobalPropertiesConstants.STATUS_OF_MISSED_VISIT.getKey());
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }
}
