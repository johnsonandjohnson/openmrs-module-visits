/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import _ from 'lodash';

import { FAILURE, REQUEST, SUCCESS } from './action-type.util';
import axiosInstance from '../components/shared/axios'
import IVisitOverview from '../shared/model/visit-overview.model';
import { handleRequest } from '../components/request-toast-handler/request-toast-handler';

export const ACTION_TYPES = {
  GET_VISITS: 'overviewVisitReducer/GET_VISITS',
  RESET: 'overviewVisitReducer/RESET',
  UPDATE_VISITS_STATUSES: 'overviewVisitReducer/UPDATE_VISITS_STATUSES'
};

const initialState = {
  visits: [] as Array<IVisitOverview>,
  loading: false,
  pages: 0,
  totalCount: 0,
  isVisitStatusesUpdateSuccess: false
};

export type OverviewVisitState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visits: action.payload.data.content,
        pages: action.payload.data.pageCount,
        totalCount: action.payload.data.totalRecords,
        loading: false
      };
    case REQUEST(ACTION_TYPES.UPDATE_VISITS_STATUSES):
      return {
        ...state,
        isVisitStatusesUpdateSuccess: false
      };
    case FAILURE(ACTION_TYPES.UPDATE_VISITS_STATUSES):
      return {
        ...state,
        isVisitStatusesUpdateSuccess: false
      };
    case SUCCESS(ACTION_TYPES.UPDATE_VISITS_STATUSES):
      return {
        ...state,
        isVisitStatusesUpdateSuccess: true
      };
    case ACTION_TYPES.RESET:
      return {
        ..._.cloneDeep(initialState)
      };
    default:
      return state;
  }
};

const moduleUrl = `/ws/visits`;

const getPageParams = (page: number, size: number, filters?: {}, query?: string) => {
  const pageParams = {
    page: page + 1,
    rows: size
  };
  // TODO: Move query out or don't add it if empty/undefined to let filters have it
  return {
    query: query,
    ...pageParams,
    ...filters
  };
}

export const getOverviewPage = (page: number, size: number, locationUuid: string, filters?: {}, query?: string) => async (dispatch) => {
  const url = `${moduleUrl}/overview/${locationUuid}`;
  const params = getPageParams(page, size, filters, query);
  await dispatch({
    type: ACTION_TYPES.GET_VISITS,
    payload: axiosInstance.get(url, { params })
  });
};

export const getVisitOverviewPage = (page: number, size: number, filters: {}, sorted: { field: string, order: 'DESC' | 'ASC' }[]) => async (dispatch) => {
  const sortedFilter = sorted.reduce((result, current) => {
    const newResult = { ...result };
    newResult[current.field + 'Sort'] = current.order;
    return newResult;
  }, {});

  const params = getPageParams(page, size, { ...filters, ...sortedFilter });

  const url = `${moduleUrl}/overview`;
  await dispatch({
    type: ACTION_TYPES.GET_VISITS,
    payload: axiosInstance.get(url, { params })
  });
};

export const reset = () => {
  return {
    type: ACTION_TYPES.RESET
  };
}

export const updateVisitStatuses = (visitUuids: string[], newVisitStatus: any, intl: any) => async (dispatch) => {
  const url = `${moduleUrl}/overview/updateVisitStatuses`;

  const body = {
    type: ACTION_TYPES.UPDATE_VISITS_STATUSES,
    payload: axiosInstance.post(url, visitUuids, {
      params: {
        newVisitStatus
      }
    })
  }

  await handleRequest(
    dispatch,
    body,
    intl.formatMessage({ id: 'visits.genericSuccess' }),
    intl.formatMessage({ id: 'visits.genericFailure' })
  );
};
