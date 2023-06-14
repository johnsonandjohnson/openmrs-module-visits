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
import {injectIntl} from 'react-intl';
import {connect} from 'react-redux';
import {PropsWithIntl} from "../../../translation/PropsWithIntl";
import {IRootState} from "../../../../reducers";
import {Button} from "react-bootstrap";
import ChangeVisitsStatusModal from "./ChangeVisitsStatusModal";

interface IProps extends DispatchProps, StateProps {
  // TODO: Use proper type of a row/visit
  selectedVisitsUuids: string[]
  isChangeVisitStatusButtonDisabled: boolean
  reloadDataCallback: () => void
}

interface IState {
  showChangeVisitsStatusModal: boolean
}

class ChangeVisitsStatusAction extends React.Component<PropsWithIntl<IProps>, IState> {
  
  state = {
    showChangeVisitsStatusModal: false
  };

  handleChangeVisitsStatusButton = () => this.setState({showChangeVisitsStatusModal: true});

  renderChangeVisitsStatusModal = () => {
    return (
      <ChangeVisitsStatusModal
        show={this.state.showChangeVisitsStatusModal}
        visits={this.props.selectedVisitsUuids}
        cancel={this.closeChangeVisitsStatusModal}
        saveVisitsStatus={this.saveVisitsStatusCallback}
      />
    );
  };

  closeChangeVisitsStatusModal = () => this.setState({ showChangeVisitsStatusModal: false });

  saveVisitsStatusCallback = () => {
    this.setState({
      showChangeVisitsStatusModal: false
    }, this.props.reloadDataCallback);
  };

  render = () => {
    return (
      <>
        {this.renderChangeVisitsStatusModal()}
        <Button
          id="change-visits-status-button"
          className="btn btn-success btn-md add-btn"
          disabled={this.props.isChangeVisitStatusButtonDisabled}
          onClick={this.handleChangeVisitsStatusButton}
        >
          {this.props.intl.formatMessage({id: "visits.overviewChangeVisitStatusLabel"})}
        </Button>
      </>
    );
  };
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(ChangeVisitsStatusAction));
