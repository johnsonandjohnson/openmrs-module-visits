/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.visits.api.dto.ErrorResponseDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.model.ErrorMessage;
import org.openmrs.module.visits.api.model.ErrorMessageEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

/**
 * Base Rest Controller All controllers in this module extend this for easy error handling
 */
public abstract class BaseRestController {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Exception handler for bad request - Http status code of 400
   *
   * @param e the exception throw
   * @return a error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponseDTO handleIllegalArgumentException(IllegalArgumentException e) {
    logger.error(e.getMessage(), e);
    return new ErrorResponseDTO(new ErrorMessage(ErrorMessageEnum.ERR_BAD_PARAM.getCode(), e.getMessage()));
  }

  /**
   * Exception handler for anything not covered above - Http status code of 500
   *
   * @param e the exception throw
   * @return a error response
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponseDTO handleException(Exception e) {
    logger.error(e.getMessage(), e);
    return new ErrorResponseDTO(new ErrorMessage(ErrorMessageEnum.ERR_SYSTEM.getCode(), e.getMessage()));
  }

  /**
   * Exception handler for bad request - Http status code of 400
   *
   * @param ex the exception throw
   * @return a error response
   */
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponseDTO handleValidationException(ValidationException ex) {
    logger.error(ex.getMessage(), ex);
    return ex.getErrorResponse();
  }

  protected Log getLogger() {
    return logger;
  }

  /**
   * Gets a string representation of an {@code obj} and escapes for HTML.
   *
   * @param obj the Object to get the escaped String representation
   * @return the String or null if obj was null
   */
  protected String toStringWithEscape(Object obj) {
    return StringEscapeUtils.escapeHtml4(Objects.toString(obj, null));
  }
}
