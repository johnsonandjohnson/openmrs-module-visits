/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.validator;

import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.module.visits.ContextMockedTest;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class EncounterValidatorTest extends ContextMockedTest {

    private EncounterValidator encounterValidator = new EncounterValidator();

    @Test
    public void shouldSupportValidation() {
        final class SubEncounter extends Encounter {
            private static final long serialVersionUID = 17L;
        }

        assertTrue(encounterValidator.supports(SubEncounter.class));
    }

    @Test
    public void shouldNotSupportValidation() {
        assertFalse(encounterValidator.supports(this.getClass()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForImproperObject() {
        Object object = new Object();
        encounterValidator.validate(object, new BindException(object, "errors"));
    }

    @Test
    public void shouldValidateEncounterWithDatetimeValidationDisabled() {
        doReturn(false)
                .when(getConfigService()).isEncounterDatetimeValidationEnabled();
        Encounter encounter = new Encounter();
        encounterValidator.validate(encounter, new BindException(encounter, "errors"));
    }
}
