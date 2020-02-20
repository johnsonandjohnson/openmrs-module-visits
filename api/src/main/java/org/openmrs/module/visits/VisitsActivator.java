/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.visits.api.util.ConfigConstants;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class VisitsActivator extends BaseModuleActivator {

    private static final Log LOGGER = LogFactory.getLog(VisitsActivator.class);

    /**
     * @see #started()
     */
    @Override
    public void started() {
        LOGGER.info("Started Visits Module");
        createGlobalSettingIfNotExists(ConfigConstants.VISIT_TIMES_KEY,
                ConfigConstants.VISIT_TIMES_DEFAULT_VALUE, ConfigConstants.VISIT_TIMES_DESCRIPTION);
        createGlobalSettingIfNotExists(ConfigConstants.VISIT_STATUSES_KEY,
                ConfigConstants.VISIT_STATUSES_DEFAULT_VALUE, ConfigConstants.VISIT_STATUSES_DESCRIPTION);
        createGlobalSettingIfNotExists(ConfigConstants.VISIT_FORM_URI_KEY,
                ConfigConstants.VISIT_FORM_URI_DEFAULT_VALUE, ConfigConstants.VISIT_FORM_URI_DESCRIPTION);
        createVisitTimeAttributeType();
        createVisitStatusAttributeType();
    }

    /**
     * @see #shutdown()
     */
    public void shutdown() {
        LOGGER.info("Shutdown Visits Module");
    }

    /**
     * @see #stopped()
     */
    @Override
    public void stopped() {
        LOGGER.info("Stopped Visits Module");
    }

    private void createVisitStatusAttributeType() {
        VisitAttributeType type = new VisitAttributeType();
        type.setName(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_NAME);
        type.setDatatypeClassname(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_DATATYPE);
        type.setDescription(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_DESCRIPTION);
        type.setUuid(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
        createVisitAttributeTypeIfNotExists(ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID, type);
    }

    private void createVisitTimeAttributeType() {
        VisitAttributeType type = new VisitAttributeType();
        type.setName(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_NAME);
        type.setDatatypeClassname(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_DATATYPE);
        type.setDescription(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_DESCRIPTION);
        type.setUuid(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID);
        createVisitAttributeTypeIfNotExists(ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID, type);
    }

    private void createVisitAttributeTypeIfNotExists(String uuid, VisitAttributeType attributeType) {
        VisitService visitService = Context.getVisitService();
        VisitAttributeType actual = visitService.getVisitAttributeTypeByUuid(uuid);
        if (actual == null) {
            visitService.saveVisitAttributeType(attributeType);
        }
    }

    private void createGlobalSettingIfNotExists(String key, String value, String description) {
        String existSetting = Context.getAdministrationService().getGlobalProperty(key);
        if (StringUtils.isBlank(existSetting)) {
            GlobalProperty gp = new GlobalProperty(key, value, description);
            Context.getAdministrationService().saveGlobalProperty(gp);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Visits Module created '%s' global property with value - %s", key, value));
            }
        }
    }
}
