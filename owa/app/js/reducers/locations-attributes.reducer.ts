/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import _ from "lodash";

import { REQUEST, SUCCESS, FAILURE } from "./action-type.util";
import axiosInstance from "../components/shared/axios";

export const ACTION_TYPES = {
  GET_LOCATIONS_ATTRIBUTES: "locationsAttributes/GET_LOCATIONS_ATTRIBUTES",
};

const initialState = {
  attributesLoading: false,
  locationsAttributes: [],
};

export type ScheduleVisitState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_LOCATIONS_ATTRIBUTES):
      return {
        ...state,
        attributesLoading: true,
      };
    case SUCCESS(ACTION_TYPES.GET_LOCATIONS_ATTRIBUTES):
      return {
        ...state,
        locationsAttributes: action.payload.data,
        attributesLoading: false,
      };
    case FAILURE(ACTION_TYPES.GET_LOCATIONS_ATTRIBUTES):
      return {
        ...state,
        attributesLoading: false,
      };
    default:
      return state;
  }
};

const baseUrl = "/ws";
const locationsAttributesUrl = `${baseUrl}/cfl/locationAttributes`;

export const getLocationAttributes = () => async (dispatch) => {
  await dispatch({
    type: ACTION_TYPES.GET_LOCATIONS_ATTRIBUTES,
    payload: axiosInstance.get(locationsAttributesUrl),
  });
};
