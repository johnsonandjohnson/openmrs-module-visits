/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { parse } from 'date-fns';
import moment from 'moment';

const TODAY = moment();
const WEEK = moment().add(6, 'days');
const MONTH = moment().add(1, 'months');
const CLEAR_DATE = null;
const ONE_DAY_IN_MILISECONDS = 24 * 60 * 60 * 1000;

export const MEDIUM_DATE_FORMAT = 'DD MMM YYYY';

export function getCommaSeparatedDateString(date?: Date, defaultValue?: string) {
  if (!!date) {
    try {
      return new Date(date).toLocaleDateString().replace(/\//g, '.');
    } catch (e) {
      console.error(e);
      return defaultValue;
    }
  }
  return defaultValue;
}

export const formatDateIfDefined = (format: string, date?: Date) => {
  return !!date ? moment(date).format(format) : date;
}

export const isDateValid = (date?: Date) =>
  !!date && date instanceof Date && !isNaN(date.valueOf());

export const parseOrNow = (date?: string): Date => {
  const parsed = parse(date ? date : Date.now());
  return isDateValid(parsed) ? parsed : parse(Date.now());
}

export const getDatesByPeriod = {
  TODAY: () => ({
    dateFrom: TODAY,
    dateTo: TODAY
  }),
  WEEK: () => ({
    dateFrom: TODAY,
    dateTo: WEEK
  }),
  MONTH: () => ({
    dateFrom: TODAY,
    dateTo: MONTH
  }),
  ALL: () => ({
    dateFrom: CLEAR_DATE,
    dateTo: CLEAR_DATE
  }),
};

export const getNumberOfDaysBetweenDates = (date1: Date, date2: Date) => {
  return Math.abs((Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate()) - 
    Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate()))) / ONE_DAY_IN_MILISECONDS;
};
