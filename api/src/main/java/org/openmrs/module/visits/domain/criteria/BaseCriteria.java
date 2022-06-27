/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;

public abstract class BaseCriteria implements Serializable {

  private static final long serialVersionUID = 6L;

  private boolean includeVoided = false;

  public abstract void loadHibernateCriteria(Criteria hibernateCriteria);

  public void initHibernateCriteria(Criteria hibernateCriteria) {
    if (!includeVoided) {
      hibernateCriteria.add(Restrictions.eq("voided", Boolean.FALSE));
    }
  }

  public void setIncludeVoided(boolean includeVoided) {
    this.includeVoided = includeVoided;
  }
}
