/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.fragment.requestmapper;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.web.BaseModuleWebContextSensitiveWithActivatorTest;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.visits.api.util.GlobalPropertiesConstants.ENCOUNTER_DATETIME_VALIDATION;

@WebAppConfiguration
public class EnterHtmlFormFragmentRequestMapperTest extends BaseModuleWebContextSensitiveWithActivatorTest {

    private static final String OLD_PROVIDER_NAME = "htmlformentryui";
    private static final String OLD_FRAGMENT_ID = "htmlform/enterHtmlForm";
    private static final String NEW_PROVIDER_NAME = "htmlformentryui2";
    private static final String NEW_FRAGMENT_ID = "htmlform/enterHtmlForm2";

    private EnterHtmlFormFragmentRequestMapper enterHtmlFormFragmentRequestMapper = new EnterHtmlFormFragmentRequestMapper();

    @Test
    public void shouldReturnTrueForMapRequestWhenOldFragment() {
        setDatetimeValidationDisableFlag(false);
        FragmentRequest request = new FragmentRequest(OLD_PROVIDER_NAME, OLD_FRAGMENT_ID);

        assertTrue(enterHtmlFormFragmentRequestMapper.mapRequest(request));
    }

    @Test
    public void shouldReturnFalseForMapRequestWhenNewFragment() {
        setDatetimeValidationDisableFlag(false);
        FragmentRequest request = new FragmentRequest(NEW_PROVIDER_NAME, NEW_FRAGMENT_ID);

        assertFalse(enterHtmlFormFragmentRequestMapper.mapRequest(request));
    }

    @Test
    public void shouldReturnFalseForMapRequestWhenOldFragmentAndValidationDisabled() {
        setDatetimeValidationDisableFlag(true);
        FragmentRequest request = new FragmentRequest(OLD_PROVIDER_NAME, OLD_FRAGMENT_ID);

        assertFalse(enterHtmlFormFragmentRequestMapper.mapRequest(request));
    }

    @Test
    public void shouldReturnFalseForMapRequestWhenNewFragmentAndValidationDisabled() {
        setDatetimeValidationDisableFlag(true);
        FragmentRequest request = new FragmentRequest(NEW_PROVIDER_NAME, NEW_FRAGMENT_ID);

        assertFalse(enterHtmlFormFragmentRequestMapper.mapRequest(request));
    }

    private void setDatetimeValidationDisableFlag(boolean isValidationDisabled) {
        Context.getAdministrationService()
        .setGlobalProperty(ENCOUNTER_DATETIME_VALIDATION.getKey(), String.valueOf(isValidationDisabled));
    }
}
