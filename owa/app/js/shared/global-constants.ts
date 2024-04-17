/** Delay in MS to apply to any refresh made after creation or an update of a Visit. It's workaround for asynchronous
 *  creation of future Visits by BE that FE has no way to know about. */
export const VISIT_SAVE_DELAY_MS = 500;

export const LOW_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME = "Low Window";
export const UP_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME = "Up Window";
export const ORIGINAL_VISIT_DATE_ATTRIBUTE_TYPE_NAME = "Original Visit Date";
export const CLINIC_CLOSED_WEEKDAYS_ATTRIBUTE_TYPE_UUID = "570e9b8f-752b-4577-9ffb-721e073387d9";
export const CLINIC_CLOSED_DATES_ATTRIBUTE_TYPE_UUID = "64b73c1c-c91a-403f-bb26-dd62dc91bfef";
