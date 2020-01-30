/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import _ from 'lodash';

import { REQUEST, SUCCESS, FAILURE } from './action-type.util';
import axiosInstance from '../config/axios';
import IVisitOverview from '../shared/model/visit-overview.model';

export const ACTION_TYPES = {
  GET_VISITS: 'overviewVisitReducer/GET_VISITS',
  GET_VISITS_PAGES_COUNT: 'overviewVisitReducer/GET_VISITS_PAGES_COUNT',
  UPDATE_SEARCH_TEXT: 'overviewVisitReducer/UPDATE_SEARCH_TEXT',
  RESET: 'overviewVisitReducer/RESET'
};

const initialState = {
  visits: [] as Array<IVisitOverview>,
  visitsLoading: false,
  visitsPagesCount: 0,
  search: ''
};

export type OverviewVisitState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visitsLoading: true
      };
    case FAILURE(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visitsLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISITS):
      return {
        ...state,
        visits: action.payload.data.content,
        visitsPagesCount: action.payload.data.pageCount,
        visitsLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISITS_PAGES_COUNT):
      return {
        ...state,
        visitsPagesCount: Math.ceil((action.payload.data.results.length / action.meta))
      };
    case ACTION_TYPES.UPDATE_SEARCH_TEXT:
        return {
          ...state,
        search: action.payload
        };
    case ACTION_TYPES.RESET:
      return {
        ..._.cloneDeep(initialState)
      };
    default:
      return state;
  }
};

const baseUrl = "/ws";
const restUrl = `${baseUrl}/rest/v1`;
const moduleUrl = `${baseUrl}/visits`;
const visitUrl = `${restUrl}/visit`;

export const getOverviewPage = (page: number, size: number, locationUuid: string) => async (dispatch) => {
  const url = `${moduleUrl}/overview/${locationUuid}`;
  await dispatch({
    type: ACTION_TYPES.GET_VISITS,
    payload: axiosInstance.get(url, {
      params: {
        page: page + 1,
        rows: size
      }
    })
  });
};

export const getOverviewPagesCount = (size: number, locationUuid: string) => async (dispatch) => {
  const url = `${visitUrl}?location=${locationUuid}&v=custom:(uuid)`;
  await dispatch({
    type: ACTION_TYPES.GET_VISITS_PAGES_COUNT,
    payload: axiosInstance.get(url),
    meta: size
  });
};

export const updateSearch = (search: string, successCallback?) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.UPDATE_SEARCH_TEXT,
    payload: search
  });
  if (successCallback) {
    successCallback();
  }
}

export const reset = (successCallback?) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.RESET
  });
  if (successCallback) {
    successCallback();
  }
}
