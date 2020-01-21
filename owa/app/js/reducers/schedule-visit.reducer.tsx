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
import IVisitType from '../shared/model/visit-type';
import VisitUI from '../shared/model/visit-ui';
import * as Msg from '../shared/utils/messages';
import { handleRequest } from '@bit/soldevelo-omrs.cfl-components.request-toast-handler';
import ILocation from '../shared/model/location';
import VisitDetailsUI from '../shared/model/visit-details-ui';

export const ACTION_TYPES = {
  GET_VISITS: 'scheduleVisitReducer/GET_VISITS',
  GET_VISIT_TYPES: 'scheduleVisitReducer/GET_VISIT_TYPES',
  GET_VISIT_TIMES: 'scheduleVisitReducer/GET_VISIT_TIMES',
  GET_VISIT_STATUSES: 'scheduleVisitReducer/GET_VISIT_STATUSES',
  GET_LOCATIONS: 'scheduleVisitReducer/GET_LOCATIONS',
  GET_VISIT: 'scheduleVisitReducer/GET_VISIT',
  UPDATE_VISIT: 'scheduleVisitReducer/UPDATE_VISIT',
  POST_VISIT: 'scheduleVisitReducer/POST_VISIT',
  RESET: 'scheduleVisitReducer/RESET'
};

const initialState = {
  visit: VisitUI.getNew(),
  visits: [] as Array<VisitDetailsUI>,
  visitsLoading: false,
  visitTypes: [] as Array<IVisitType>,
  visitTimes: [] as Array<string>,
  visitStatuses: [] as Array<string>,
  locations: [] as Array<ILocation>
};

export type ScheduleVisitState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_VISIT):
    case FAILURE(ACTION_TYPES.POST_VISIT):
    case SUCCESS(ACTION_TYPES.POST_VISIT):
    case REQUEST(ACTION_TYPES.GET_VISIT_TYPES):
    case FAILURE(ACTION_TYPES.GET_VISIT_TYPES):
    case REQUEST(ACTION_TYPES.GET_VISIT_TIMES):
    case FAILURE(ACTION_TYPES.GET_VISIT_TIMES):
    case REQUEST(ACTION_TYPES.GET_VISIT_STATUSES):
    case FAILURE(ACTION_TYPES.GET_VISIT_STATUSES):
    case REQUEST(ACTION_TYPES.GET_LOCATIONS):
    case FAILURE(ACTION_TYPES.GET_LOCATIONS):
    case REQUEST(ACTION_TYPES.GET_VISIT):
    case FAILURE(ACTION_TYPES.GET_VISIT):
      return {
        ...state,
      };
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
        visits: action.payload.data.results.map(r => new VisitDetailsUI(r)),
        visitsLoading: false
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_TYPES):
      return {
        ...state,
        visitTypes: action.payload.data.results
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT):
      return {
        ...state,
        visit: new VisitUI(action.payload.data)
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_TIMES):
      return {
        ...state,
        visitTimes: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_STATUSES):
      return {
        ...state,
        visitStatuses: action.payload.data,
        visit: _.assign(state.visit, { visitStatus: action.payload.data[0] })
      };
    case SUCCESS(ACTION_TYPES.GET_LOCATIONS):
      return {
        ...state,
        locations: action.payload.data.results
      };
    case ACTION_TYPES.UPDATE_VISIT: {
      return {
        ...state,
        visit: action.payload
      };
    }
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
const visitTypeUrl = `${restUrl}/visittype`;
const visitUrl = `${restUrl}/visit`;
const locationUrl = `${restUrl}/location`;
const visitsTimesUrl = `${moduleUrl}/times`;
const visitsStatusesUrl = `${moduleUrl}/statuses`;

export const getVisitTypes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_TYPES,
    payload: axiosInstance.get(visitTypeUrl)
  });
};

export const getVisitTimes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_TIMES,
    payload: axiosInstance.get(visitsTimesUrl)
  });
};

export const getVisitStatuses = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_STATUSES,
    payload: axiosInstance.get(visitsStatusesUrl)
  });
};

export const getLocations = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_LOCATIONS,
    payload: axiosInstance.get(locationUrl)
  });
};

export const updateVisit = (visit: VisitUI) => async (dispatch) => {
  dispatch({
    type: ACTION_TYPES.UPDATE_VISIT,
    payload: await visit.validate(false)
  })
};

export const postVisit = (visit: VisitUI, successCallback?) => async (dispatch) => {
  const validated = await visit.validate(true);
  if (_.isEmpty(validated.errors)) {
    const body = {
      type: ACTION_TYPES.POST_VISIT,
      payload: axiosInstance.post(visit.uuid ? `${visitUrl}/${visit.uuid}` : visitUrl, visit.toModel())
    }
    await handleRequest(dispatch, body, Msg.GENERIC_SUCCESS, Msg.GENERIC_FAILURE);
    if (successCallback) {
      successCallback();
    }
  } else {
    dispatch({
      type: ACTION_TYPES.UPDATE_VISIT,
      payload: validated
    })
  }
};

const visitRepresentation = 'custom:(uuid,attributes,startDatetime,visitType,display,location,patient)';

export const getVisit = (visitUuid: string) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT,
    payload: axiosInstance.get(`${visitUrl}/${visitUuid}?v=${visitRepresentation}`)
  });
};

export const getVisits = (patientUuid: string) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISITS,
    payload: axiosInstance.get(`${visitUrl}?patient=${patientUuid}&v=${visitRepresentation}`)
  });
};

export const reset = (successCallback?) => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.RESET
  });
  if (successCallback) {
    successCallback();
  }
}
