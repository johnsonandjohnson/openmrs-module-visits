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
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import { PropsWithIntl } from '../../components/translation/PropsWithIntl';
import _ from "lodash";
import { RouteComponentProps } from 'react-router-dom';
import IModalParams from "../manage-visits/modal-params";
import { IRootState } from '../../reducers';
import {
  getVisitTypes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisitTimes,
  getVisit,
  getVisitStatuses,
  reset
} from "../../reducers/schedule-visit.reducer";
import { getExtraInfoModalEnabledGP, getHolidayWeekdaysGP } from "../../reducers/global-property.reducer"
import {
  Form,
  FormGroup,
  FormControl,
  Col,
  Button,
  Row,
  Modal,
} from "react-bootstrap";
import ErrorDesc from '../error-description/error-desc';
import FormLabel from '../form-label/form-label';
import OpenMrsDatePicker from '../openmrs-date-picker/openmrs-date-picker';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircle, faTimes } from "@fortawesome/free-solid-svg-icons";

import "../schedule-visit/schedule-visit-modal.scss"
import ExtraInformationModal from "./extra-information-modal";
import { getNumberOfDaysBetweenDates, visitDatesTheSame } from "../../shared/utils/date-util";

interface IProps extends DispatchProps, StateProps, RouteComponentProps {
  show: boolean;
  modalParams: IModalParams | null;
  patientUuid: string;
  visitUuid?: string;
  locale?: string
  confirm: (modalParams: IModalParams | null) => void;
  cancel: () => void;
  refetchVisits: () => void;
}

interface IState {
  isSaveButtonDisabled: boolean;
  showExtraInfoModal: boolean;
}

const FORM_CLASS = 'form-control';
const ERROR_FORM_CLASS = FORM_CLASS + ' error-field';

class ScheduleVisitModal extends React.PureComponent<PropsWithIntl<IProps>, IState> {
  state = {
    isSaveButtonDisabled: true,
    showExtraInfoModal: false
  };

  componentDidMount() {
    if (this.isEdit()) {
      this.loadComponent();
      this.props.getVisit(this.props.visitUuid!);
    } else {
      this.props.reset(this.loadComponent);
    }
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (!!this.props.visitUuid && this.props.visitUuid !== prevProps.visitUuid) {
      this.loadComponent();
      this.props.getVisit(this.props.visitUuid!);
    } else if (!!prevProps.visitUuid && this.props.visitUuid === null) {
      this.props.reset(this.loadComponent);
    }
  }

  loadComponent = () => {
    this.props.getVisitTypes();
    this.props.getLocations();
    this.props.getVisitTimes();
    this.props.getVisitStatuses();
    this.props.getExtraInfoModalEnabledGP();
    this.props.getHolidayWeekdaysGP();

    this.handleChange(this.props.patientUuid, "patientUuid");
  };

  isEdit = () => !!this.props.visitUuid;

  validate = () =>
    this.props.visit.validate(true, this.props.intl, this.isEdit()).then((visit) => {
      this.setState({ isSaveButtonDisabled: !!Object.keys(visit.errors).length });
    });

  handleChange = (newValue: string, prop: string) => {
    const cloned = _.cloneDeep(this.props.visit);
    cloned[prop] = newValue;
    cloned.touchedFields[prop] = true;
    this.props.updateVisit(cloned, this.props.intl);
  };

  handleSave = () => this.props.saveVisit(this.props.visit, this.props.intl, this.saveVisitCallback);

  saveVisitCallback = () => {
    this.closeModal();
    this.closeExtraInfoModal();
    this.props.refetchVisits();
    this.refreshPage();
  };

  refreshPage = () => {
    window.location.reload();
  };

  closeModal = () => {
    if (!this.isEdit()) {
      this.props.reset(this.loadComponent);
    }
    this.props.cancel();
  };

  renderDropdown = (
    errors,
    label: string,
    fieldName: string,
    options: Array<React.ReactFragment>,
    required?: boolean
  ) => (
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} mandatory={required} locale={this.props.locale} />
      <FormControl
        componentClass="select"
        name={fieldName}
        value={this.props.visit[fieldName]}
        onChange={(e) => this.handleChange((e.target as HTMLInputElement).value, fieldName)}
        className={errors && errors[fieldName] ? ERROR_FORM_CLASS : FORM_CLASS}
      >
        <option value="" />
        {options}
      </FormControl>
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>
  );

  renderDatePicker = (errors, label: string, fieldName: string) => (
    <FormGroup controlId={fieldName}>
      <FormLabel label={label} />
      <OpenMrsDatePicker
        value={this.props.visit[fieldName]}
        onChange={(isoDate) => this.handleChange(isoDate, fieldName)}
      />
      {errors && <ErrorDesc field={errors[fieldName]} />}
    </FormGroup>
  );

  renderVisitDate = (errors) =>
    this.renderDatePicker(
      errors,
      this.props.intl.formatMessage({
        id: "visits.visitPlannedDateLabel"
      }),
      "startDate"
    );

  renderActualDate = (errors) =>
    this.renderDatePicker(
      errors,
      this.props.intl.formatMessage({
        id: "visits.visitActualDateLabel"
      }),
      "actualDate"
    );

  renderLocation = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.locationLabel" }),
      "location",
      this.props.locations.map((type) => (
        <option value={type.uuid} key={type.uuid}>
          {type.display}
        </option>
      ))
    );

  renderVisitType = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitTypeLabel" }),
      "type",
      this.props.visitTypes.filter(type => !type.retired).map((type) => (
        <option value={type.uuid} key={type.uuid}>
          {type.display}
        </option>
      )),
      true
    );

  renderVisitStatus = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitStatusLabel" }),
      "status",
      this.props.visitStatuses.map((status) => (
        <option value={status} key={status}>
          {status}
        </option>
      )),
      true
    );

  renderVisitTime = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitTimeLabel" }),
      "time",
      this.props.visitTimes.map((time) => (
        <option value={time} key={time}>
          {time}
        </option>
      ))
    );

  openExtraInfoModal = () => {
    this.setState({ showExtraInfoModal: true })
  };

  renderSaveButton = () => {
    const isExtraInformationEnabled = this.props.isExtraInformationEnabled!['value'];
    return (
      <Button
        id="schedule-visit-save"
        className="btn btn-success btn-md pull-right confirm"
        onClick={isExtraInformationEnabled === 'true' ? this.openExtraInfoModal : this.handleSave}
        disabled={this.state.isSaveButtonDisabled}
      >
        {this.props.intl.formatMessage({
          id: this.isEdit() ? "visits.saveButtonLabel" : "visits.scheduleVisitButtonLabel",
          defaultMessage: this.props.intl.formatMessage({ id: "visits.saveButtonLabel"}),
        })}
      </Button>
    );
  };

  renderCancelButton = () => (
    <Button id="schedule-visit-cancel" onClick={this.closeModal}>
      <span className="fa-stack fa-2x">
        <FontAwesomeIcon icon={faCircle} className="fa-stack-2x icon-button-background" />
        <FontAwesomeIcon icon={faTimes} className="fa-stack-1x" />
      </span>
    </Button>
  );

  buildModal = () => {
    const { show, cancel } = this.props;
    const { errors } = this.props.visit;
    return (
      <Modal id="schedule-visit-modal" show={show} onHide={cancel}>
        <Modal.Body>
          <div className="modal-title">
            {this.isEdit()
              ? this.props.intl.formatMessage({ id: "visits.editVisit" })
              : this.props.intl.formatMessage({ id: "visits.scheduleVisit" })}
          </div>
          <Form className="fields-form">
            <Row>
              <Col md={12}>
                {this.renderVisitDate(errors)}
                {this.renderVisitTime(errors)}
                {this.renderLocation(errors)}
                {this.renderVisitType(errors)}
                {this.isEdit() && this.renderVisitStatus(errors)}
              </Col>
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

  findNumberOfDaysBetweenCurrentAndNearestFutureVisit = (allVisitDates: Date[], currentVisitDate: Date) => {
    const laterVisits = allVisitDates.filter(date => date > currentVisitDate);
    
    if (!laterVisits.length) {
      return null;
    }

    const searchedDate = new Date(Math.min.apply(null, laterVisits));

    return getNumberOfDaysBetweenDates(currentVisitDate, searchedDate);
  }

  findNumberOfDaysBetweenCurrentAndNearestPastVisit = (allVisitDates: Date[], currentVisitDate: Date) => {
    const previousVisits = allVisitDates.filter(date => date < currentVisitDate);
      
    if (!previousVisits.length) {
      return null;
    }

    const searchedDate = new Date(Math.max.apply(null, previousVisits));

    return getNumberOfDaysBetweenDates(currentVisitDate, searchedDate);
  }

  isVisitForThisDayDuplicated = (allVisitDates: Date[], currentVisitDate: Date) => {
    
    return allVisitDates.some(date => visitDatesTheSame(date, currentVisitDate));
  }

  getPatientVisitsDates = () => {
    let { patientVisits } = this.props;

    if (this.isEdit()) {
      patientVisits = patientVisits.filter(({ uuid }) => uuid != this.props.visitUuid);
    }

    const allPatientVisitsDates = [] as Date[];
    patientVisits.forEach(({ startDate }) => allPatientVisitsDates.push(new Date(startDate)));

    return allPatientVisitsDates;
  }

  renderExtraInfoModal = () => {
    const { visit, holidayWeekdays, isExtraInformationEnabled } = this.props;  
    
    if (!isExtraInformationEnabled || !holidayWeekdays) {
      return;
    }

    const holidayWeekdaysValue: string = holidayWeekdays!['value'];
    const currentVisitWeekday = new Date(visit.startDate).toLocaleDateString('en-us', { weekday: 'long' });
    const isDayHolidayWeekday = holidayWeekdaysValue.split(",").includes(currentVisitWeekday);
    const currentVisitDate = new Date(visit.startDate);
    const patientVisitsDates = this.getPatientVisitsDates();
    const modalParams = {
      currentVisitDate: visit.startDate,
      currentVisitWeekday,
      precedingVisitDaysNumber: this.findNumberOfDaysBetweenCurrentAndNearestPastVisit(patientVisitsDates, currentVisitDate),
      nextVistitDaysNumber: this.findNumberOfDaysBetweenCurrentAndNearestFutureVisit(patientVisitsDates, currentVisitDate),
      isDayHolidayWeekday,
      isVisitDateDuplicated: this.isVisitForThisDayDuplicated(patientVisitsDates, currentVisitDate)
    }

    return (
      <ExtraInformationModal
        show={this.state.showExtraInfoModal}
        modalParams={modalParams}
        confirm={this.confirmSavingVisitOnExtraInfoModal}
        cancel={this.closeExtraInfoModal}
        locale={this.props.locale}
      />
    );
  }

  confirmSavingVisitOnExtraInfoModal = () => {
    this.handleSave();
  }

  closeExtraInfoModal = () => {
    this.setState({ showExtraInfoModal: false })
  }

  render() {
    this.validate();
    return (
      <>
        {this.props.show ? this.buildModal() : null}
        {this.renderExtraInfoModal()}
      </>
    );
  }
}

const mapStateToProps = ({ scheduleVisit, globalPropertyReducer }: IRootState) => ({
  visit: scheduleVisit.visit,
  visitTypes: scheduleVisit.visitTypes,
  visitStatuses: scheduleVisit.visitStatuses,
  visitTimes: scheduleVisit.visitTimes,
  locations: scheduleVisit.locations,
  patientVisits: scheduleVisit.visits,
  isExtraInformationEnabled: globalPropertyReducer.isExtraInfoModalEnabled,
  holidayWeekdays: globalPropertyReducer.holidayWeekdays
});

const mapDispatchToProps = ({
  getVisitTypes,
  getVisitTimes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisit,
  getVisitStatuses,
  reset,
  getExtraInfoModalEnabledGP,
  getHolidayWeekdaysGP
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduleVisitModal));