/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import java.util.List;

public abstract class BaseOpenMRSEntityDAO<E extends BaseOpenmrsMetadata> {

  private DbSessionFactory dbSessionFactory;

  private final Class<E> mappedClass;

  BaseOpenMRSEntityDAO(Class<E> mappedClass) {
    this.mappedClass = mappedClass;
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  DbSession getSession() {
    return dbSessionFactory.getCurrentSession();
  }

  E getById(Integer id) {
    return mappedClass.cast(getSession().get(mappedClass, id));
  }

  E getByUuid(String uuid) {
    return mappedClass.cast(
        getSession().createCriteria(mappedClass).add(Restrictions.eq("uuid", uuid)).uniqueResult());
  }

  E getByName(String name) {
    return mappedClass.cast(
        getSession().createCriteria(mappedClass).add(Restrictions.eq("name", name)).uniqueResult());
  }

  List<E> getAll(boolean includeRetired) {
    Criteria criteria =
        getSession()
            .createCriteria(mappedClass)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

    if (!includeRetired) {
      criteria.add(Restrictions.eq("retired", Boolean.FALSE));
    }

    return criteria.list();
  }

  long countAll(boolean includeRetired) {
    Criteria criteria =
        getSession()
            .createCriteria(mappedClass)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .setProjection(Projections.rowCount());

    if (!includeRetired) {
      criteria.add(Restrictions.eq("retired", Boolean.FALSE));
    }

    return (long) criteria.uniqueResult();
  }

  E save(E obj) {
    getSession().saveOrUpdate(obj);
    return obj;
  }

  void delete(E obj) {
    getSession().delete(obj);
  }
}
