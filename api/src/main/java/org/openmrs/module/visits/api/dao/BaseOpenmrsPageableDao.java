package org.openmrs.module.visits.api.dao;

import java.util.List;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.BaseCriteria;

public interface BaseOpenmrsPageableDao<T extends BaseOpenmrsData> extends OpenmrsDataDAO<T> {

    List<T> findAllByCriteria(BaseCriteria criteria, PagingInfo pagingInfo);
}
