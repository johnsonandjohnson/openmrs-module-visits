/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import { } from '../../reducers/schedule-visit.reducer';
import { Form, ControlLabel } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import {
  SCHEDULE_VISIT
} from '../../shared/utils/messages';

interface IReactProps {}

interface IProps extends IReactProps, DispatchProps, RouteComponentProps<{
  patientUuid: string
}> {}

interface IState {
}

class ScheduleVisit extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
  }

  render = () => {
    return (
      <div>
        <Form className="fields-form">
          <ControlLabel className="fields-form-title">
            <h4>{SCHEDULE_VISIT}</h4>
          </ControlLabel>
        </Form>
      </div>
    );
  };
}

const mapDispatchToProps = ({
});

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  undefined,
  mapDispatchToProps
)(ScheduleVisit);
