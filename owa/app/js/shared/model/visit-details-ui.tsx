import IVisit from "./visit";
import _ from 'lodash';
import { VISIT_TIME_ATTRIBUTE_UUID } from "./visit-time-attribute";
import IAttributeDetails from './attribute-details';
import { getCommaSeparatedDateString } from '../utils/date-util';
import { VISIT_STATUS_ATTRIBUTE_UUID } from './visit-status-attribute';

export default class VisitDetailsUI {
  private _uuid: string;
  private _visitDate: string;
  private _visitTime: string;
  private _location: string;
  private _visitType: string;
  private _status: string;

  constructor(baseObject: IVisit) {
    this._uuid = baseObject.uuid;
    this._visitDate = getCommaSeparatedDateString(baseObject.startDatetime, '')!;
    const timeAttributes = _.findLast(baseObject.attributes,
      (a: IAttributeDetails) => a.attributeType.uuid === VISIT_TIME_ATTRIBUTE_UUID);
    this._visitTime = _.get(timeAttributes, 'value', '');
    this._location = _.get(baseObject, 'location.display', '');
    this._visitType = _.get(baseObject, 'visitType.display', '');
    const statusAttribute = _.findLast(baseObject.attributes,
      (a: IAttributeDetails) => a.attributeType.uuid === VISIT_STATUS_ATTRIBUTE_UUID);
    this._status = _.get(statusAttribute, 'value', 'Unknown');
  }

  get uuid() { return this._uuid };
  get visitDate() { return this._visitDate };
  get visitTime() { return this._visitTime };
  get location() { return this._location };
  get visitType() { return this._visitType };
  get status() { return this._status };
}
