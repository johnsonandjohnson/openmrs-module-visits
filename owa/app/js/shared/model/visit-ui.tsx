import IVisit from "./visit";
import { ObjectUI } from "@bit/soldevelo-omrs.cfl-components.base-model";
import { IForm } from "@bit/soldevelo-omrs.cfl-components.validation/model/form";
import * as Yup from "yup";
import _ from 'lodash';
import * as Msg from '../../shared/utils/messages';
import { validateFormSafely } from '@bit/soldevelo-omrs.cfl-components.validation';
import IVisitRequest from "./visit-request";

export default class VisitUI extends ObjectUI<IVisitRequest> implements IVisitRequest, IForm {
  patient: string;
  visitType: string;
  location: string;

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

  private static convertToRequest(baseObject: IVisit): IVisitRequest {
    return {
      visitType: baseObject.visitType.uuid,
      location: baseObject.location.uuid,
      patient: ""
    } as IVisitRequest;
  }

  static getNew(): VisitUI {
    return new VisitUI({
      visitType: {},
      location: {}
    } as IVisit);
  }

  getValidationSchema(validateNotTouched: boolean): Yup.ObjectSchema {
    const validators = {
      visitType: Yup.string().test('mandatory check', Msg.FIELD_REQUIRED,
        v => this.validateRequiredField('visitType', v, validateNotTouched))
    };

    return Yup.object().shape(validators);
  }

  validateRequiredField(key: string, value: string, validateNotTouched: boolean): boolean {
    if (this.touchedFields[key] || validateNotTouched) {
      return !!(value && value.trim());
    }
    return true;
  }
}
