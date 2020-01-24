package org.openmrs.module.visits.api.service;

import java.util.List;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.domain.criteria.BaseCriteria;

public interface BaseOpenmrsCriteriaDataService<T extends BaseOpenmrsData> extends OpenmrsDataService<T> {

    List<T> findAllByCriteria(BaseCriteria criteria);

    List<T> findAllByCriteria(BaseCriteria criteria, PagingInfo paging);
}

