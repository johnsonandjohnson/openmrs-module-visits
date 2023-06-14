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
import { injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import IVisitsOverviewActionData from "../data/IVisitsOverviewActionData";
import ScheduleVisitAction from "./actions/ScheduleVisitAction";
import ChangeVisitsStatusAction from "./actions/ChangeVisitsStatusAction";

interface IVisitsOverviewActionsBarProps extends DispatchProps, StateProps {
  actions: IVisitsOverviewActionData
  patientUuid: string
  selectedVisitsUuids: string[]
  isChangeVisitStatusButtonDisabled: boolean
  reloadDataCallback: () => void
  openScheduleVisitModalCallback: (visitUuid: string | null) => void
}

interface IVisitsOverviewActionsBarState {

}

class VisitsOverviewActionsBar extends React.Component<PropsWithIntl<IVisitsOverviewActionsBarProps>, IVisitsOverviewActionsBarState> {
  renderChangeVisitsStatusAction = () => {
    return (
      <ChangeVisitsStatusAction
        selectedVisitsUuids={this.props.selectedVisitsUuids}
        isChangeVisitStatusButtonDisabled={this.props.isChangeVisitStatusButtonDisabled}
        reloadDataCallback={this.props.reloadDataCallback}
      />
    );
  };

  renderScheduleNewVisitAction = () => {
    return (
      <ScheduleVisitAction patientUuid={this.props.patientUuid}
                           openScheduleVisitModalCallback={this.props.openScheduleVisitModalCallback}/>
    );
  };

  render = () => {
    return (
      <div className="visits-overview-actions-bar">
        {this.props.actions.changeVisitsStatus ? this.renderChangeVisitsStatusAction() : null}
        {this.props.actions.scheduleNewVisit ? this.renderScheduleNewVisitAction() : null}
      </div>
    );
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(VisitsOverviewActionsBar));
