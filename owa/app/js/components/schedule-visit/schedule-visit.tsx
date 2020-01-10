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
import { getVisitTypes, getLocations, updateVisit, postVisit } from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import { Form, ControlLabel, FormGroup, FormControl, Col, Button } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import ErrorDesc from '@bit/soldevelo-omrs.cfl-components.error-description';
import FormLabel from '@bit/soldevelo-omrs.cfl-components.form-label';
import {
  SCHEDULE_VISIT,
  VISIT_TYPE_LABEL,
  SAVE_BUTTON_LABEL,
  LOCATION_LABEL
} from '../../shared/utils/messages';
import _ from 'lodash';
import './schedule-visit.scss';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string
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
    this.props.getVisitTypes();
    this.props.getLocations();
    this.handleChange(this.props.match.params.patientUuid, 'patient');
  }

  handleChange = (newValue: string, prop: string) => {
    const cloned = _.cloneDeep(this.props.visit);
    cloned[prop] = newValue;
    cloned.touchedFields[prop] = true;
    this.props.updateVisit(cloned);
  }

  handleSave = () => {
    this.props.postVisit(this.props.visit, this.props.history.goBack);
  }

  renderDropdown = (errors, label: string, fieldName: string, options: Array<React.ReactFragment>) =>
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} mandatory />
      <FormControl componentClass="select" name={fieldName}
        value={this.props.visit[fieldName]}
        onChange={e => this.handleChange((e.target as HTMLInputElement).value, fieldName)}
        className={errors && errors[fieldName] ? ERROR_FORM_CLASS : FORM_CLASS}>
        <option value=""></option>
        {options}
      </FormControl>
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>

  renderLocation = (errors) =>
    this.renderDropdown(errors, LOCATION_LABEL, 'location',
      this.props.locations.map(type =>
        <option value={type.uuid} key={type.uuid}>{type.display}</option>
      ));

  renderVisitType = (errors) =>
    this.renderDropdown(errors, VISIT_TYPE_LABEL, 'visitType',
      this.props.visitTypes.map(type =>
        <option value={type.uuid} key={type.uuid}>{type.display}</option>
      ));

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
            <h2>{SCHEDULE_VISIT}</h2>
          </ControlLabel>
          <Col md={3} >
            {this.renderLocation(errors)}
            {this.renderVisitType(errors)}
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
  locations: scheduleVisit.locations
});

const mapDispatchToProps = ({
  getVisitTypes,
  getLocations,
  updateVisit,
  postVisit
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduleVisit);
