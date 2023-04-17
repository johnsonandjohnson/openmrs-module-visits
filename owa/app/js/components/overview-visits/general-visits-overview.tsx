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
import { PropsWithIntl } from '../translation/PropsWithIntl';
import { IRootState } from '../../reducers';
import { RouteComponentProps } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import VisitsOverview from '../visits-overview';
import { VISITS_GENERAL_OVERVIEW_APP_ID } from '../visits-overview/constants';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{ patientUuid: string }> {

}

interface IState {

}

class GeneralVisitsOverview extends React.Component<PropsWithIntl<IProps>, IState> {
  render = () => {
    return (
      <>
        <div className="body-wrapper">
          <div className="content">
            <div className="visits-overview">
              <h2>{this.props.intl.formatMessage({ id: "visits.overviewTittle" })}</h2>
              <div className="helper-text">
                {this.props.intl.formatMessage({
                  id: "visits.overviewDescription"
                })}
              </div>
              {!!this.props.sessionLocation ?
                <VisitsOverview
                  appName={VISITS_GENERAL_OVERVIEW_APP_ID}
                  locationUuid={this.props.sessionLocation?.uuid}/> : null}
            </div>
          </div>
        </div>
      </>
    )
  }
}

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

const mapStateToProps = ({ openmrs }: IRootState) => ({
  sessionLocation: openmrs.session.sessionLocation
});

const mapDispatchToProps = ({

});


export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(GeneralVisitsOverview));
