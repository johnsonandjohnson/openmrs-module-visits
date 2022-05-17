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

import org.apache.commons.validator.routines.UrlValidator;

public final class UriUtils {
    private static final String PREFIX_URL = "https://example.org";
    private static final UrlValidator VALIDATOR = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);

    public static boolean isUriValid(String uri) {
        return VALIDATOR.isValid(PREFIX_URL + uri);
    }

    private UriUtils() {
    }
}
