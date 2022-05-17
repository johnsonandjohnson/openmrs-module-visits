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

import org.springframework.util.ObjectUtils;

import java.util.Map;

public final class TemplateUtils {
  private static final String START_PARENTHESIS = "{{";
  private static final String END_PARENTHESIS = "}}";

  private TemplateUtils() {}

  public static String fillTemplate(String template, Map<String, String> params) {
    String result = template;
    for (Map.Entry<String, String> param : params.entrySet()) {
      result = fillTemplate(result, param.getKey(), param.getValue());
    }
    return result;
  }

  static String fillTemplate(String template, String param, String value) {
    return template.replace(
        START_PARENTHESIS + param + END_PARENTHESIS, ObjectUtils.nullSafeToString(value));
  }
}
