/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import Header from '../patient-header/header';
import React from 'react';
import { RouteComponentProps } from 'react-router-dom';
import ScheduleVisit from '../schedule-visit/schedule-visit';
import ManageVisits from '../manage-visits/manage-visits';

interface IWrappedComponentProps extends RouteComponentProps<{ patientUuid: string }> {
  isNew?: boolean;
}

const withPatientHeader = (WrappedComponent) => {
  return class extends React.Component<IWrappedComponentProps> {
    render() {
      return (
          <div className="body-wrapper">
            <Header patientUuid={this.props.match.params.patientUuid} />
            <div className="content">
              <WrappedComponent  {...this.props}/>
            </div>
          </div>
      )
    }
  }
}

export const ScheduleVisitWithHeader = withPatientHeader(ScheduleVisit);
export const ManageVisitsWithHeader = withPatientHeader(ManageVisits); 
