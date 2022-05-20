/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import org.junit.Test;
import org.openmrs.module.visits.ContextMockedTest;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.model.ErrorMessage;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ValidationExceptionTest extends ContextMockedTest {

    private static final String ERROR_MESSAGE = "ERROR MESSAGE";
    public static final String ERROR_DESCRIPTION = "ERROR DESCRIPTION";

    @Test
    public void shouldReturnErrorResponseForStringConstructor() {
        ValidationException validationException = new ValidationException(ERROR_MESSAGE);
        assertEquals(ERROR_MESSAGE, validationException.getErrorResponse().getError());
    }

    @Test
    public void shouldReturnErrorResponseForConstructorWithStringAndErrorMessages() {
        List<ErrorMessage> errorMessagesList = Arrays.asList(
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION),
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION),
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION)
        );

        ValidationException validationException = new ValidationException(
                ERROR_MESSAGE,
                errorMessagesList
        );
        assertThat(validationException.getErrorResponse().getError(), equalTo(ERROR_MESSAGE));
        assertThat(validationException.getErrorResponse().getErrorMessages(), contains(errorMessagesList.toArray()));
    }

    @Test
    public void shouldReturnErrorResponseForConstructorWithErrorMessages() {
        List<ErrorMessage> errorMessagesList = Arrays.asList(
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION),
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION),
                new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION)
        );

        ValidationException validationException = new ValidationException(
                errorMessagesList
        );
        assertThat(validationException.getErrorResponse().getErrorMessages(), contains(errorMessagesList.toArray()));
    }

    @Test
    public void shouldReturnErrorResponseForConstructorWithOneErrorMessage() {
        ErrorMessage errorMessage = new ErrorMessage(ERROR_MESSAGE, ERROR_DESCRIPTION);

        ValidationException validationException = new ValidationException(errorMessage);
        assertThat(validationException.getErrorResponse().getErrorMessages(), contains(errorMessage));
    }
}
