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

export const ACTION_TYPES = {
  RESET: 'patientReducer/RESET'
};

const initialState = {
};

export type PatientState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.RESET:
      return {
        ..._.cloneDeep(initialState)
      };
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
