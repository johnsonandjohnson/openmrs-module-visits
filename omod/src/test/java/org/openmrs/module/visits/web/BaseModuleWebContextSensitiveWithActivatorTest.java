package org.openmrs.module.visits.web;

import org.junit.After;
import org.junit.Before;
import org.openmrs.module.visits.VisitsActivator;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public abstract class BaseModuleWebContextSensitiveWithActivatorTest extends BaseModuleWebContextSensitiveTest {

    private VisitsActivator activator;

    @Before
    public void setUpWithActivator() {
        activator = new VisitsActivator();
        activator.started();
    }

    @After
    public void stopActivator() {
        activator.stopped();
    }

}
