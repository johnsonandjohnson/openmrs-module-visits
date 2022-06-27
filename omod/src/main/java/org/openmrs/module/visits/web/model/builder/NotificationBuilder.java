/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.model.builder;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.visits.web.model.Notification;

/**
 * Builds the notification DTO based on provided information.
 */
public class NotificationBuilder {

    private String errorMessage;
    private String infoMessage;

    public Notification build() {
        boolean isErrorMessage = StringUtils.isNotBlank(this.errorMessage);
        return new Notification()
                .setErrorMessage(isErrorMessage)
                .setMessage(isErrorMessage ? this.errorMessage : this.infoMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public NotificationBuilder withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public NotificationBuilder withInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
        return this;
    }
}
