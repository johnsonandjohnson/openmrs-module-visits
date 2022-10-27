/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { REQUEST, SUCCESS, FAILURE } from './action-type.util';
import axios from 'axios';

export const ACTION_TYPES = {
  GET_EXTRA_INFO_MODAL_ENABLED_GP: 'globalProperty/GET_EXTRA_INFO_MODAL_ENABLED_GP',
  GET_HOLIDAY_WEEKDAYS_GP: 'globalProperty/GET_HOLIDAY_WEEKDAYS_GP'
}

const initialState = {
  isExtraInfoModalEnabled: null,
  holidayWeekdays: null,
  loading: false,
  success: false
};

export type GlobalPropertyState = Readonly<typeof initialState>

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_EXTRA_INFO_MODAL_ENABLED_GP):
      return {
        ...state,
        loading: true,
        success: false
      };
    case FAILURE(ACTION_TYPES.GET_EXTRA_INFO_MODAL_ENABLED_GP):
      return {
        ...state,
        loading: false,
        success: false
      };
    case SUCCESS(ACTION_TYPES.GET_EXTRA_INFO_MODAL_ENABLED_GP):
      const extraInfoModalEnabledGPResults = action.payload.data.results;
      return {
        ...state,
        isExtraInfoModalEnabled: extraInfoModalEnabledGPResults?.length ? extraInfoModalEnabledGPResults[0] : state.isExtraInfoModalEnabled,
        loading: false,
        success: true
      };
      case REQUEST(ACTION_TYPES.GET_HOLIDAY_WEEKDAYS_GP):
        return {
          ...state,
          loading: true,
          success: false
        };
      case FAILURE(ACTION_TYPES.GET_HOLIDAY_WEEKDAYS_GP):
        return {
          ...state,
          loading: false,
          success: false
        };
      case SUCCESS(ACTION_TYPES.GET_HOLIDAY_WEEKDAYS_GP):
        const holidayWeekdaysResults = action.payload.data.results;
        return {
          ...state,
          holidayWeekdays: holidayWeekdaysResults?.length ? holidayWeekdaysResults[0] : state.holidayWeekdays,
          loading: false,
          success: true
        };
      default:
        return state;
  }
};

const baseUrl = "/openmrs/ws/rest/v1/systemsetting"

//actions
export const getExtraInfoModalEnabledGP = () => {
  const requestUrl = `${baseUrl}?q=visits.extraSchedulingInformationEnabled&v=default`;
  return {
    type: ACTION_TYPES.GET_EXTRA_INFO_MODAL_ENABLED_GP,
    payload: axios.get(requestUrl)
  };
};

export const getHolidayWeekdaysGP = () => {
  const requestUrl = `${baseUrl}?q=visits.holidayWeekdays&v=default`;
  return {
    type: ACTION_TYPES.GET_HOLIDAY_WEEKDAYS_GP,
    payload: axios.get(requestUrl)
  };
};

