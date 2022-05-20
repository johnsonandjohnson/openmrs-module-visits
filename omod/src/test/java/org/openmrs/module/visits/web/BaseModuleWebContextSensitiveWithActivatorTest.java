/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web;

import org.junit.After;
import org.junit.Before;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.visits.VisitsActivator;
import org.openmrs.module.visits.api.util.ConfigConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseModuleWebContextSensitiveWithActivatorTest extends BaseModuleWebContextSensitiveTest {

    private VisitsActivator activator;

    @Before
    public void setUpWithActivator() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DaemonToken daemonToken = getDaemonToken();
        activator = new VisitsActivator();
        activator.setDaemonToken(daemonToken);
        activator.started();
    }

    @After
    public void stopActivator() {
        activator.stopped();
    }

    private DaemonToken getDaemonToken() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Module module = new Module(ConfigConstants.MODULE_ID);
        module.setModuleId(ConfigConstants.MODULE_ID);
        Method method = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        method.setAccessible(true);
        return (DaemonToken) method.invoke(null, module);
    }
}
