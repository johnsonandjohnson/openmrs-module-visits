/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.model;

import org.junit.Test;
import org.openmrs.module.visits.ContextMockedTest;

import static org.junit.Assert.assertEquals;

public class ErrorMessageTest extends ContextMockedTest {

    private static final String SAMPLE_TEXT = "Some text";

    @Test
    public void shouldSetCodeProperly() {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCode(SAMPLE_TEXT);
        assertEquals(SAMPLE_TEXT, errorMessage.getCode());
    }

    @Test
    public void shouldSetMessageProperly() {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(SAMPLE_TEXT);
        assertEquals(SAMPLE_TEXT, errorMessage.getMessage());
    }
}
