/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import _ from 'lodash';
import { connect } from 'react-redux';
import {
  getPersonStatus,
  closeModal,
  openModal,
  putPersonStatus,
  getPossibleReasons
} from './person-status.reducer'
import './person-status.scss';
import ChangeStatusModal from './modal/change-status-modal';
import { PersonStatusUI } from './model/person-status.model-ui';
import * as Msg from './constants';
import { injectIntl } from 'react-intl';

interface IPersonStatusProps extends DispatchProps, StateProps {
  patientUuid: string;
  intl: any;
}

interface IPersonStatusState {
}

class PersonStatus extends React.PureComponent<IPersonStatusProps, IPersonStatusState> {

  constructor(props: IPersonStatusProps) {
    super(props);
  }

  componentDidMount() {
    this.props.getPersonStatus(this.props.patientUuid);
    this.props.getPossibleReasons();
  };

  getStatusLabel = (key, set) => {
    return _.get(set, `${key}.label`, key);
  };

  handleClose = () => {
    this.props.closeModal();
  };

  reloadPage = () => {
    window.location.reload();
  }

  handleConfirm = (value: string, reason?: string) => {
    const status = _.cloneDeep(this.props.personStatus.status);
    status.value = value;
    status.reason = reason;
    this.props.putPersonStatus(status, this.props.intl);
    this.props.closeModal();
    this.reloadPage();
  };

  handleChangeStatus = (event) => {
    this.props.openModal(event.target.id);
  };

  renderStatus = () => {
    const { personStatus } = this.props;
    return (
      <p>{this.props.intl.formatMessage({ id: "person.status.update.label", defaultMessage: 'Status' }) + ': ' + this.getStatusLabel(personStatus.status.value, Msg.PERSON_STATUSES)}</p>
    );
  };

  prepereStatusForModal = (status: PersonStatusUI): PersonStatusUI => {
    const modalStatus = _.cloneDeep(status);
    modalStatus.value = this.getInitialValue(modalStatus);
    return modalStatus;
  };

  getInitialValue = (status: PersonStatusUI) => (!!status.value &&
    status.value != Msg.MISSING_VALUE_KEY && status.value != Msg.NO_CONSENT_KEY) ? status.value : Msg.ACTIVATED_KEY;

  render() {
    const { status, showModal, submitDisabled, possibleResults } = this.props.personStatus;
    return (
      <>
        <ChangeStatusModal
          cancel={this.handleClose}
          confirm={this.handleConfirm}
          show={showModal}
          submitDisabled={submitDisabled}
          status={this.prepereStatusForModal(status)}
          possibleResults={possibleResults} />
        <button className='btn-secondary' onClick={this.handleChangeStatus}>
          {this.props.intl.formatMessage({ id: "cfl.updateStatusButton.label", defaultMessage: 'Update the status' })}
        </button>
      </>
    );
  };
}

const mapStateToProps = ({ personStatus }: any) => ({
  personStatus
});

const mapDispatchToProps = ({
  getPersonStatus,
  closeModal,
  openModal,
  putPersonStatus,
  getPossibleReasons
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

// @ts-ignore
export default injectIntl(connect(mapStateToProps,mapDispatchToProps)(PersonStatus));
