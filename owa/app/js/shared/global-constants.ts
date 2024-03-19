/** Delay in MS to apply to any refresh made after creation or an update of a Visit. It's workaround for asynchronous
 *  creation of future Visits by BE that FE has no way to know about. */
export const VISIT_SAVE_DELAY_MS = 500;

export const LOW_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME = 'Low Window';
export const UP_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME = 'Up Window';
