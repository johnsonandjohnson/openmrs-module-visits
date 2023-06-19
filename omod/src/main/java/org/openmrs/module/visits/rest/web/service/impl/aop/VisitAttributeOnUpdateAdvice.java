/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.rest.web.service.impl.aop;

import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.service.VisitOverviewResourceService;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VisitAttributeOnUpdateAdvice implements MethodBeforeAdvice {
  private static final Set<String> WATCHED_METHODS = new HashSet<>(
      Arrays.asList("saveVisitAttributeType", "retireVisitAttributeType", "unretireVisitAttributeType",
          "purgeVisitAttributeType"));

  @Override
  public void before(Method method, Object[] objects, Object o) {
    if (WATCHED_METHODS.contains(method.getName())) {
      Context.getService(VisitOverviewResourceService.class).invalidateCache();
    }
  }
}
