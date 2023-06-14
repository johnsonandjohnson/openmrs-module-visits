/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { PropsWithIntl } from '../../components/translation/PropsWithIntl';
import { IRootState } from '../../reducers';
import { RouteComponentProps } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import VisitsOverview from '../visits-overview';
import { VISITS_PATIENT_OVERVIEW_APP_ID } from '../visits-overview/constants';
import Header from '../person-header/person-header';
import PersonStatus from '../person-status/person-status';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{ patientUuid: string }> {

}

interface IState {

}

class PatientVisitsOverview extends React.Component<PropsWithIntl<IProps>, IState> {
  render = () => {
    const newProps = {
      ...this.props,
      patientUuid: this.props.match.params.patientUuid,
      dashboardType: "PATIENT",
      redirectUrl: getBaseUrl() + "coreapps/clinicianfacing/patient.page?patientId="
        + this.props.match.params.patientUuid,
      displayTelephone: true
    };
    return (
      <>
        <div className="body-wrapper">
          <div className="content">
            <Header {...newProps}>
              <PersonStatus patientUuid={newProps.patientUuid}/>
            </Header>
            <div className="visits-overview">
              <h2>{this.props.intl.formatMessage({ id: "visits.manageVisits" })}</h2>
              {!!this.props.sessionLocation ?
                <VisitsOverview
                  appName={VISITS_PATIENT_OVERVIEW_APP_ID}
                  patientUuid={this.props.match.params.patientUuid}
                /> : null}
            </div>
          </div>
        </div>
      </>
    )
  }
}

const getBaseUrl = () => {
  const path = window.location.pathname;
  return path.substring(0, path.indexOf('/owa/')) + '/';
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

const mapStateToProps = ({ openmrs }: IRootState) => ({
  sessionLocation: openmrs.session.sessionLocation
});

const mapDispatchToProps = ({});


export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(PatientVisitsOverview));
