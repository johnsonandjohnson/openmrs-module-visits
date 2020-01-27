import { parse } from 'date-fns';

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

export const isDateValid = (date?: Date) =>
  !!date && date instanceof Date && !isNaN(date.valueOf());

export const parseOrNow = (date?: string): Date => {
  const parsed = parse(date ? date : Date.now());
  return isDateValid(parsed) ? parsed : parse(Date.now());
}
