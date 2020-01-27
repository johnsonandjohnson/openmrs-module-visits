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
import { getVisitTypes, getLocations, updateVisit, saveVisit, getVisitTimes, getVisit, getVisitStatuses, reset } from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import { Form, ControlLabel, FormGroup, FormControl, Col, Button } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import ErrorDesc from '@bit/soldevelo-omrs.cfl-components.error-description';
import FormLabel from '@bit/soldevelo-omrs.cfl-components.form-label';
import {
  SCHEDULE_VISIT,
  EDIT_VISIT,
  VISIT_TYPE_LABEL,
  VISIT_STATUS_LABEL,
  VISIT_TIME_LABEL,
  VISIT_DATE_LABEL,
  SAVE_BUTTON_LABEL,
  MANAGE_VISITS,
  LOCATION_LABEL
} from '../../shared/utils/messages';
import _ from 'lodash';
import './schedule-visit.scss';
import OpenMrsDatePicker from '@bit/soldevelo-omrs.cfl-components.openmrs-date-picker';
import { history } from '../../config/redux-store';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string,
  visitUuid?: string
}> { }

interface IState {
}

const FORM_CLASS = 'form-control';
const ERROR_FORM_CLASS = FORM_CLASS + ' error-field';

class ScheduleVisit extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
  }

  componentDidMount() {
    if (this.isEdit()) {
      this.loadComponent();
      this.props.getVisit(this.props.match.params.visitUuid!);
    } else {
      this.props.reset(this.loadComponent);
    }
  }

  loadComponent = () => {
    this.props.getVisitTypes();
    this.props.getLocations();
    this.props.getVisitTimes();
    this.props.getVisitStatuses();
    this.handleChange(this.props.match.params.patientUuid, 'patient');
  }

  isEdit = () => !!this.props.match.params.visitUuid

  handleChange = (newValue: string, prop: string) => {
    const cloned = _.cloneDeep(this.props.visit);
    cloned[prop] = newValue;
    cloned.touchedFields[prop] = true;
    this.props.updateVisit(cloned);
  }

  handleSave = () => {
    this.props.saveVisit(this.props.visit, this.props.history.goBack);
  }

  handleManageVisitsButton = () => {
    history.push(`/visits/manage/${this.props.match.params.patientUuid}`);
  }

  renderDropdown = (errors, label: string, fieldName: string, options: Array<React.ReactFragment>, required?: boolean) =>
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} mandatory={required} />
      <FormControl componentClass="select" name={fieldName}
        value={this.props.visit[fieldName]}
        onChange={e => this.handleChange((e.target as HTMLInputElement).value, fieldName)}
        className={errors && errors[fieldName] ? ERROR_FORM_CLASS : FORM_CLASS}>
        <option value=""></option>
        {options}
      </FormControl>
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>

  renderDatePicker = (errors, label: string, fieldName: string) =>
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} />
      <OpenMrsDatePicker
        value={this.props.visit[fieldName]}
        onChange={isoDate => this.handleChange(isoDate, fieldName)}
      />
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>

  renderVisitDate = (errors) =>
    this.renderDatePicker(errors, VISIT_DATE_LABEL, 'startDatetime');

  renderLocation = (errors) =>
    this.renderDropdown(errors, LOCATION_LABEL, 'location',
      this.props.locations.map(type =>
        <option value={type.uuid} key={type.uuid}>{type.display}</option>
      ));

  renderVisitType = (errors) =>
    this.renderDropdown(errors, VISIT_TYPE_LABEL, 'visitType',
      this.props.visitTypes.map(type =>
        <option value={type.uuid} key={type.uuid}>{type.display}</option>
      ), true);

  renderVisitStatus = (errors) =>
    this.renderDropdown(errors, VISIT_STATUS_LABEL, 'visitStatus',
      this.props.visitStatuses.map(status =>
        <option value={status} key={status}>{status}</option>
      ), true);

  renderVisitTime = (errors) =>
    this.renderDropdown(errors, VISIT_TIME_LABEL, 'visitTime',
      this.props.visitTimes.map(time =>
        <option value={time} key={time}>{time}</option>
      ));

  renderManageVisitsButton = () =>
    <Button
      className="btn btn-secondary btn-md pull-right"
      onClick={this.handleManageVisitsButton}>
      {MANAGE_VISITS}
    </Button>


  renderSaveButton = () =>
    <Button
      className="btn btn-success btn-md"
      onClick={this.handleSave}>
      {SAVE_BUTTON_LABEL}
    </Button>

  render = () => {
    const { errors } = this.props.visit;

    return (
      <div className="scheduled-visit">
        <Form className="fields-form">
          <ControlLabel className="fields-form-title">
            <h2>{this.isEdit() ? EDIT_VISIT : SCHEDULE_VISIT}</h2>
          </ControlLabel>
          <FormGroup>
            {this.renderManageVisitsButton()}
          </FormGroup>
          <Col md={3}>
            {this.renderVisitDate(errors)}
            {this.renderVisitTime(errors)}
            {this.renderLocation(errors)}
            {this.renderVisitType(errors)}
            {this.isEdit() && this.renderVisitStatus(errors)}
          </Col>
        </Form>
        {this.renderSaveButton()}
      </div>
    );
  };
}

const mapStateToProps = ({ scheduleVisit }: IRootState) => ({
  visit: scheduleVisit.visit,
  visitTypes: scheduleVisit.visitTypes,
  visitStatuses: scheduleVisit.visitStatuses,
  visitTimes: scheduleVisit.visitTimes,
  locations: scheduleVisit.locations
});

const mapDispatchToProps = ({
  getVisitTypes,
  getVisitTimes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisit,
  getVisitStatuses,
  reset
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduleVisit);
