export function getCommaSeparatedDateString(date? : Date, defaultValue?: string) {
  if (!!date) {
    try {
      return new Date(date).toLocaleDateString().replace(/\//g,'.');
    } catch (e) {
      console.error(e);
      return defaultValue;
    }
  }
  return defaultValue;
}
