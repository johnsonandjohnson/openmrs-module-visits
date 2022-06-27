/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.model;

/** System notification DTO object. */
public class Notification extends AbstractDTO {

  private static final long serialVersionUID = 12L;

  /** Indicates the notification type. */
  private boolean errorMessage;

  /** Provides the notification message. */
  private String message;

  public boolean isErrorMessage() {
    return errorMessage;
  }

  public Notification setErrorMessage(boolean errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public Notification setMessage(String message) {
    this.message = message;
    return this;
  }

  @Override
  public String toString() {
    return "Notification{" + "errorMessage=" + errorMessage + ", message='" + message + '\'' + '}';
  }
}
