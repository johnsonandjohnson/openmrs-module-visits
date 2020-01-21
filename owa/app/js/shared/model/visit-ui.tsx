import IVisit from "./visit";
import { ObjectUI } from "@bit/soldevelo-omrs.cfl-components.base-model";
import { IForm } from "@bit/soldevelo-omrs.cfl-components.validation/model/form";
import * as Yup from "yup";
import _ from 'lodash';
import * as Msg from '../../shared/utils/messages';
import { validateFormSafely } from '@bit/soldevelo-omrs.cfl-components.validation';
import IVisitRequest from "./visit-request";
import VisitTimeAttribute, { VISIT_TIME_ATTRIBUTE_UUID } from "./visit-time-attribute";
import { convertToUtcString, convertToLocalString } from '../utils/time-util';
import moment from "moment";
import VisitStatusAttribute, { VISIT_STATUS_ATTRIBUTE_UUID } from "./visit-status-attribute";
import IAttributeDetails from "./attribute-details";

export default class VisitUI extends ObjectUI<IVisitRequest> implements IVisitRequest, IForm {
  uuid?: string;
  patient: string;
  visitType: string;
  visitTime?: string;
  visitStatus: string;
  location?: string;
  startDatetime?: string;

  errors: { [key: string]: string; };
  touchedFields: { [key: string]: boolean; };

  constructor(baseObject: IVisit) {
    super(VisitUI.convertToRequest(baseObject) as IVisitRequest);
    this.errors = {};
    this.touchedFields = {};
  }

  async validate(validateNotTouched: boolean): Promise<VisitUI> {
    const schema = this.getValidationSchema(validateNotTouched);

    const validationResult = await validateFormSafely(this, schema);

    const visit = _.clone(this);
    visit.errors = validationResult;
    return visit;
  }

  toModel(): IVisitRequest {
    return {
      patient: this.uuid ? undefined : this.patient,
      visitType: this.visitType,
      location: this.location,
      startDatetime: convertToUtcString(this.startDatetime ? this.startDatetime : moment.now()),
      attributes: this.visitTime ?
        [new VisitTimeAttribute(this.visitTime), new VisitStatusAttribute(this.visitStatus)]
        : [new VisitStatusAttribute(this.visitStatus)]
    } as IVisitRequest;
  }

  static getNew(): VisitUI {
    return new VisitUI({
      visitType: {}
    } as IVisit);
  }

  getValidationSchema(validateNotTouched: boolean): Yup.ObjectSchema {
    const validators = {
      visitType: Yup.string().test('mandatory check', Msg.FIELD_REQUIRED,
        v => this.validateRequiredField('visitType', v, validateNotTouched)),
      visitStatus: Yup.string().test('mandatory check', Msg.FIELD_REQUIRED,
        v => this.validateRequiredField('visitStatus', v, validateNotTouched))
    };

    return Yup.object().shape(validators);
  }

  validateRequiredField(key: string, value: string, validateNotTouched: boolean): boolean {
    if (this.touchedFields[key] || validateNotTouched) {
      return !!(value && value.trim());
    }
    return true;
  }

  private static findAttribute(typeUuid: string, attributes?: Array<IAttributeDetails>) {
    return attributes ? _.find(attributes, (a) => a.attributeType.uuid === typeUuid) : undefined;
  }

  private static convertToRequest(baseObject: IVisit): IVisitRequest {
    const visitTime = this.findAttribute(VISIT_TIME_ATTRIBUTE_UUID, baseObject.attributes);
    const visitStatus = this.findAttribute(VISIT_STATUS_ATTRIBUTE_UUID, baseObject.attributes);
    return {
      uuid: baseObject.uuid,
      visitType: baseObject.visitType.uuid,
      startDatetime: convertToLocalString(baseObject.startDatetime),
      location: baseObject.location ? baseObject.location.uuid : undefined,
      visitTime: visitTime ? visitTime.value : undefined,
      visitStatus: visitStatus ? visitStatus.value : "",
      patient: baseObject.patient ? baseObject.patient.uuid : ""
    } as IVisitRequest;
  }
}