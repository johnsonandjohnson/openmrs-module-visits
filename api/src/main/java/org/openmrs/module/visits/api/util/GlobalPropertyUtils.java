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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;

import java.util.Collection;

public final class GlobalPropertyUtils {

    private static Log log = LogFactory.getLog(GlobalPropertyUtils.class);

    public static String getGlobalProperty(String key) {
        return Context.getAdministrationService().getGlobalProperty(key, null);
    }

    public static void createGlobalSettingsIfNotExists(Collection<GPDefinition> definitions) {
        for (GPDefinition definition : definitions) {
            createGlobalSettingIfNotExists(definition);
        }
    }

    public static void createGlobalSettingIfNotExists(GPDefinition definition) {
        createGlobalSettingIfNotExists(definition.getKey(), definition.getDefaultValue(), definition.getDescription());
    }

    private static void createGlobalSettingIfNotExists(String key, String value, String description) {
        String existSetting = getGlobalProperty(key);
        if (StringUtils.isBlank(existSetting)) {
            GlobalProperty gp = new GlobalProperty(key, value, description);
            Context.getAdministrationService().saveGlobalProperty(gp);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Visits Module created '%s' global property with value - %s", key, value));
            }
        }
    }

    public static Integer getInteger(String key) {
        String keyValue = getGlobalProperty(key);
        return Integer.valueOf(keyValue);
    }

    private GlobalPropertyUtils() { }
}
