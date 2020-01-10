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

export const ACTION_TYPES = {
  GET_VISIT_TYPES: 'scheduleVisitReducer/GET_VISIT_TYPES',
  GET_LOCATIONS: 'scheduleVisitReducer/GET_LOCATIONS',
  UPDATE_VISIT: 'scheduleVisitReducer/UPDATE_VISIT',
  POST_VISIT: 'scheduleVisitReducer/POST_VISIT',
  RESET: 'scheduleVisitReducer/RESET'
};

const initialState = {
  visit: VisitUI.getNew(),
  visitTypes: [] as Array<IVisitType>,
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
    case REQUEST(ACTION_TYPES.GET_LOCATIONS):
    case FAILURE(ACTION_TYPES.GET_LOCATIONS):
      return {
        ...state,
      };
    case SUCCESS(ACTION_TYPES.GET_VISIT_TYPES):
      return {
        ...state,
        visitTypes: action.payload.data.results
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

const baseUrl = "/ws/rest/v1/";
const visitTypeUrl = `${baseUrl}visittype`;
const visitUrl = `${baseUrl}visit`;
const locationUrl = `${baseUrl}location`;

export const getVisitTypes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_VISIT_TYPES,
    payload: axiosInstance.get(visitTypeUrl)
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
      payload: axiosInstance.post(visitUrl, visit.toModel())
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

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
