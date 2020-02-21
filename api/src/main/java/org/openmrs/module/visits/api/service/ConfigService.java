package org.openmrs.module.visits.api.service;

import java.util.List;

/**
 * Provides the module configuration set
 */
public interface ConfigService {

    /**
     * Provides the number of days after which missed visits should be automatically marked as missed.
     * @return - the number of days
     */
    int getMinimumVisitDelayToMarkItAsMissed();

    /**
     * Provides the list specifying statuses which point on already completed visits. Visits with these statuses should
     * not be changed to missed.
     * @return - the list with visit statuses
     */
    List<String> getStatusesEndingVisit();

    /**
     * Provides the value specifying a status that will be set if the visit will be determined to be marked as missing.
     * @return - the visit status
     */
    String getStatusOfMissedVisit();
}