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

import org.junit.Test;
import org.openmrs.module.visits.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UriUtilsTest extends BaseTest {

    @Test
    public void shouldReturnTrueIfUriIsValid() {
        String uri = "/patient/123";
        boolean actual = UriUtils.isUriValid(uri);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseIfUriContainsDoubleSlashes() {
        String uri = "//patient/123";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseIfUriHasSpaces() {
        String uri = "/patient /123";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseIfUriHasNotReplacedParam() {
        String uri = "/patient/{{param}}";
        boolean actual = UriUtils.isUriValid(uri);
        assertFalse(actual);
    }
}
