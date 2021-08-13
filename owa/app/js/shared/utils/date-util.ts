import { parse } from 'date-fns';
import moment from 'moment';

const today = moment();
const week = moment().add(6, 'days');
const month = moment().add(1, 'months');
const clearDate = null;

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
    dateFrom: today,
    dateTo: today
  }),
  WEEK: () => ({
    dateFrom: today,
    dateTo: week
  }),
  MONTH: () => ({
    dateFrom: today,
    dateTo: month
  }),
  ALL: () => ({
    dateFrom: clearDate,
    dateTo: clearDate
  }),
};
