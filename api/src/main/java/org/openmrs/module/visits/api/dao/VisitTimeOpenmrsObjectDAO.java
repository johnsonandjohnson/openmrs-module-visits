/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsMetadataDAO;
import org.openmrs.module.visits.api.entity.VisitTime;

import java.util.List;

public class VisitTimeOpenmrsObjectDAO<E extends OpenmrsObject>
    extends HibernateOpenmrsMetadataDAO<VisitTime> {

  private final Class<E> objectClass;

  private DbSessionFactory dbSessionFactory;

  VisitTimeOpenmrsObjectDAO(Class<E> objectClass) {
    super(VisitTime.class);
    this.objectClass = objectClass;
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  DbSession getSession() {
    return dbSessionFactory.getCurrentSession();
  }

  E getByName(String name) {
    return objectClass.cast(
        getSession().createCriteria(objectClass).add(Restrictions.eq("name", name)).uniqueResult());
  }

  List<E> getByGroup(String groupName) {
    Criteria criteria =
        getSession().createCriteria(objectClass).add(Restrictions.eq("group", groupName));

    return criteria.list();
  }

  long countAll(boolean includeRetired) {
    Criteria criteria =
        getSession()
            .createCriteria(objectClass)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .setProjection(Projections.rowCount());

    if (!includeRetired) {
      criteria.add(Restrictions.eq("retired", Boolean.FALSE));
    }

    return (long) criteria.uniqueResult();
  }
}
