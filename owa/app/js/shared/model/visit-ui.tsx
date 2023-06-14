/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import {ObjectUI} from "../../components/base-model/object-ui";
import {IForm} from "../../components/validation/model/form";
import * as Yup from "yup";
import _ from 'lodash';
import {validateFormSafely} from '../../components/validation/validation';
import IVisitDetails from "./visit-details";
import { Props } from "@fortawesome/react-fontawesome";

export default class VisitUI extends ObjectUI<IVisitDetails> implements IVisitDetails, IForm {
  uuid: string;
  startDate: Date;
  time: string | null;
  location: string | null;
  type: string;
  status: string | null;
  actualDate?: Date | undefined;

  errors: { [key: string]: string; };
  touchedFields: { [key: string]: boolean; };

  constructor(baseObject: IVisitDetails) {
    super(baseObject);
    this.errors = {};
    this.touchedFields = {};
  }

  async validate(validateNotTouched: boolean, intl: any, isEdit: boolean): Promise<VisitUI> {
    const schema = this.getValidationSchema(validateNotTouched, intl, isEdit);
    
    const validationResult = await validateFormSafely(this, schema);

    const visit = _.clone(this);
    visit.errors = validationResult;
    return visit;
  }

  getValidationSchema(validateNotTouched: boolean, intl: any, isEdit: boolean): Yup.ObjectSchema {
    const createValidators = {
      type: Yup.string().test('mandatory check', intl.formatMessage({ id: "visits.fieldRequired" }),
        v => this.validateRequiredField('type', v, validateNotTouched)),
      location: Yup.string().test('mandatory check', intl.formatMessage({ id: "visits.fieldRequired" }),
        v => this.validateRequiredField('location', v, validateNotTouched)),
    };
    const editValidators = {
      type: Yup.string().test('mandatory check', intl.formatMessage({ id: "visits.fieldRequired" }),
        v => this.validateRequiredField('type', v, validateNotTouched)),
      status: Yup.string().test('mandatory check', intl.formatMessage({ id: "visits.fieldRequired" }),
        v => this.validateRequiredField('status', v, validateNotTouched)),
      location: Yup.string().test('mandatory check', intl.formatMessage({ id: "visits.fieldRequired" }),
        v => this.validateRequiredField('location', v, validateNotTouched)),
    };

    return Yup.object().shape(isEdit? editValidators : createValidators);
  }

  validateRequiredField(key: string, value: string, validateNotTouched: boolean): boolean {
    if (this.touchedFields[key] || validateNotTouched) {
      return !!(value && value.trim());
    }
    return true;
  }

  static getNew(): VisitUI {
    return new VisitUI({
      startDate: new Date()
    } as IVisitDetails);
  }

}
