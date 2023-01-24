/*
 *  This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *  <p>
 *  Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 *  graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { ControlLabel } from 'react-bootstrap';

import './form-label.scss';
import { REQUIRED_FORM_FIELD } from './constants';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

interface FormLabel {
  label: string,
  mandatory?: boolean,
  locale?: string
}

const FormLabel: React.SFC<FormLabel> = (props: FormLabel) => {
  return (
    <span className='form-label'>
      <ControlLabel>
        {props.label}
        {!!props.mandatory
          ? <p className="label-required">{`(${getIntl(props.locale).formatMessage({id: "REQUIRED_LABEL", defaultMessage: REQUIRED_FORM_FIELD})})`}</p>
          : null}
      </ControlLabel>
    </span>
  );
}

FormLabel.defaultProps = {
  label: '',
  mandatory: false
}

export default FormLabel;
