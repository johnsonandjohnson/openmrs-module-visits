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
