/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from "react";
import { connect } from "react-redux";
import { injectIntl } from "react-intl";
import { PropsWithIntl } from "../../../translation/PropsWithIntl";
import { IRootState } from "../../../../reducers";
import { getVisitStatuses, reset, saveVisit } from "../../../../reducers/schedule-visit.reducer";
import { Button, Col, Form, FormControl, FormGroup, Modal, Row } from "react-bootstrap";
import FormLabel from "../../../form-label/form-label";
import { updateVisitStatuses } from "../../../../reducers/overview-visits.reducer";
import "../../../schedule-visit/schedule-visit-modal.scss";

interface IProps extends DispatchProps, StateProps {
  show: boolean;
  visits: string[];
  cancel: () => void;
  saveVisitsStatus: () => void;
  visitStatus: string;
}

interface IState {
  isSaveButtonDisabled: boolean;
  newVisitStatus: string;
  saveInProgress: boolean;
}

class ChangeVisitsStatusModal extends React.PureComponent<PropsWithIntl<IProps>, IState> {
  state = {
    isSaveButtonDisabled: true,
    newVisitStatus: "",
    saveInProgress: false,
  };

  componentDidMount() {
    this.props.reset(this.loadComponent);
  }

  loadComponent = () => {
    this.props.getVisitStatuses();
  };

  handleSave = () => {
    this.setState({ saveInProgress: true });
    this.props.updateVisitStatuses(
      this.props.visits,
      this.state.newVisitStatus,
      this.props.intl,
      this.saveVisitCallback,
    );
  };

  saveVisitCallback = () => {
    this.setState({ saveInProgress: false }, () => {
      this.closeModal();
      this.props.saveVisitsStatus();
    });
  };

  closeModal = () => {
    this.props.reset(this.loadComponent);
    this.props.cancel();
  };

  handleChange = (newValue: string) => {
    this.setState({
      newVisitStatus: newValue,
      isSaveButtonDisabled: !newValue,
    });
  };

  renderDropdown = (label: string, fieldName: string, options: Array<React.ReactFragment>, required?: boolean) => (
    <FormGroup controlId={fieldName}>
      <FormLabel
        label={label}
        mandatory={required}
      />
      <FormControl
        componentClass="select"
        name={fieldName}
        value={this.props.visitStatus}
        onChange={(e) => this.handleChange((e.target as HTMLInputElement).value)}
        className="form-control"
      >
        <option value="" />
        {options}
      </FormControl>
    </FormGroup>
  );

  renderVisitStatus = () =>
    this.renderDropdown(
      this.props.intl.formatMessage({ id: "visits.overviewSetSelectedVisitsLabelPart1" }) +
        " " +
        this.props.visits.length +
        " " +
        this.props.intl.formatMessage({ id: "visits.overviewSetSelectedVisitsLabelPart2" }),
      "status",
      this.props.visitStatuses.map((status) => (
        <option
          value={status}
          key={status}
        >
          {status}
        </option>
      )),
      false,
    );

  renderSaveButton = () => {
    return (
      <Button
        id="schedule-visit-save"
        className="btn btn-success btn-md pull-right confirm"
        onClick={this.handleSave}
        disabled={this.state.isSaveButtonDisabled}
      >
        {this.state.saveInProgress ? (
          <i className="icon-spinner icon-spin icon-2x" />
        ) : (
          this.props.intl.formatMessage({
            id: "common.confirm",
          })
        )}
      </Button>
    );
  };

  renderCancelButton = () => (
    <Button
      id="schedule-visit-cancel"
      onClick={this.closeModal}
    >
      {this.props.intl.formatMessage({ id: "common.cancel" })}
    </Button>
  );

  buildModal = () => {
    const { show, cancel } = this.props;
    return (
      <Modal
        id="schedule-visit-modal"
        show={show}
        onHide={cancel}
      >
        <Modal.Body>
          <div className="modal-title">
            {this.props.intl.formatMessage({ id: "visits.overviewChangeVisitStatusLabel" })}
          </div>
          <Form className="fields-form">
            <Row>
              <Col md={12}>{this.renderVisitStatus()}</Col>
            </Row>
            <Row>
              <FormGroup className="control-buttons">
                {this.renderCancelButton()}
                {this.renderSaveButton()}
              </FormGroup>
            </Row>
          </Form>
        </Modal.Body>
      </Modal>
    );
  };

  render() {
    return <>{this.props.show ? this.buildModal() : null}</>;
  }
}

const mapStateToProps = ({ scheduleVisit }: IRootState) => ({
  visitStatuses: scheduleVisit.visitStatuses,
});

const mapDispatchToProps = {
  saveVisit,
  getVisitStatuses,
  reset,
  updateVisitStatuses,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(mapStateToProps, mapDispatchToProps)(ChangeVisitsStatusModal));
