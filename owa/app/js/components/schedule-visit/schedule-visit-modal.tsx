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
import { PropsWithIntl } from "../../components/translation/PropsWithIntl";
import _ from "lodash";
import { RouteComponentProps } from "react-router-dom";
import IModalParams from "../manage-visits/modal-params";
import { IRootState } from "../../reducers";
import {
  getLocations,
  getVisit,
  getVisitStatuses,
  getVisitTimes,
  getVisitTypes,
  reset,
  saveVisit,
  updateVisit,
} from "../../reducers/schedule-visit.reducer";
import { getSession } from "../../reducers/session";
import {
  getExtraInfoModalEnabledGP,
  getOutsideDateWindowInfoModalEnabledGP,
  getVisitTypeUuidsWithTimeWindowGP,
} from "../../reducers/global-property.reducer";
import { Button, Col, Form, FormControl, FormGroup, Modal, Row } from "react-bootstrap";
import ErrorDesc from "../error-description/error-desc";
import FormLabel from "../form-label/form-label";
import OpenMrsDatePicker from "../openmrs-date-picker/openmrs-date-picker";
import "../schedule-visit/schedule-visit-modal.scss";
import ExtraInformationModal from "./extra-information-modal";
import {
  MEDIUM_DATE_FORMAT,
  ONE_DAY_IN_MILISECONDS,
  formatDateIfDefined,
  getNumberOfDaysBetweenDates,
  visitDatesTheSame,
} from "../../shared/utils/date-util";
import {
  CLINIC_CLOSED_DATES_ATTRIBUTE_TYPE_UUID,
  CLINIC_CLOSED_WEEKDAYS_ATTRIBUTE_TYPE_UUID,
  LOW_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME,
  ORIGINAL_VISIT_DATE_ATTRIBUTE_TYPE_NAME,
  UP_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME,
  VISIT_SAVE_DELAY_MS,
} from "../../shared/global-constants";
import moment from "moment";

interface IProps extends DispatchProps, StateProps, RouteComponentProps {
  show: boolean;
  modalParams: IModalParams | null;
  patientUuid: string;
  visitUuid?: string;
  locale?: string;
  confirm: (modalParams: IModalParams | null) => void;
  cancel: () => void;
  refetchVisits: () => void;
}

interface IState {
  isSaveButtonDisabled: boolean;
  showExtraInfoModal: boolean;
  saveInProgress: boolean;
}

const FORM_CLASS = "form-control";
const ERROR_FORM_CLASS = FORM_CLASS + " error-field";

class ScheduleVisitModal extends React.PureComponent<PropsWithIntl<IProps>, IState> {
  state = {
    isSaveButtonDisabled: true,
    showExtraInfoModal: false,
    saveInProgress: false,
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

    if (!this.isEdit() && prevProps.sessionLocation !== this.props.sessionLocation && !this.props.visit.location) {
      this.handleChange(this.props.sessionLocation?.uuid, "location");
    }
  }

  loadComponent = () => {
    this.props.getSession();
    this.props.getVisitTypes();
    this.props.getLocations();
    this.props.getVisitTimes();
    this.props.getVisitStatuses();
    this.props.getExtraInfoModalEnabledGP();
    this.props.getOutsideDateWindowInfoModalEnabledGP();
    this.props.getVisitTypeUuidsWithTimeWindowGP();

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

  handleSave = () => {
    this.setState({ isSaveButtonDisabled: true, saveInProgress: true });
    this.props.saveVisit(this.props.visit, this.props.intl, () =>
      setTimeout(this.saveVisitCallback, VISIT_SAVE_DELAY_MS),
    );
  };

  saveVisitCallback = () => {
    this.setState({ saveInProgress: false }, () => {
      this.closeModal();
      this.closeExtraInfoModal();
      this.props.refetchVisits();
    });
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
    required?: boolean,
    disabled?: boolean,
  ) => (
    <FormGroup controlId={fieldName}>
      <FormLabel
        label={label}
        mandatory={required}
      />
      <FormControl
        componentClass="select"
        name={fieldName}
        value={this.props.visit[fieldName]}
        onChange={(e) => this.handleChange((e.target as HTMLInputElement).value, fieldName)}
        className={errors && errors[fieldName] ? ERROR_FORM_CLASS : FORM_CLASS}
        disabled={disabled}
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
        id: "visits.visitPlannedDateLabel",
      }),
      "startDate",
    );

  renderLocation = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.locationLabel" }),
      "location",
      this.props.locations.map((location) => (
        <option
          value={location.uuid}
          key={location.uuid}
        >
          {location.display}
        </option>
      )),
      true,
    );

  renderVisitType = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitTypeLabel" }),
      "type",
      this.props.visitTypes
        .filter((type) => !type.retired)
        .map((type) => (
          <option
            value={type.uuid}
            key={type.uuid}
          >
            {type.display}
          </option>
        )),
      true,
      this.props.visit.actualDate != null,
    );

  renderVisitStatus = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitStatusLabel" }),
      "status",
      this.props.visitStatuses.map((status) => (
        <option
          value={status}
          key={status}
        >
          {status}
        </option>
      )),
      true,
    );

  renderVisitTime = (errors) =>
    this.renderDropdown(
      errors,
      this.props.intl.formatMessage({ id: "visits.visitTimeLabel" }),
      "time",
      this.props.visitTimes.map((time) => (
        <option
          value={time}
          key={time}
        >
          {time}
        </option>
      )),
    );

  openExtraInfoModal = () => {
    this.setState({ showExtraInfoModal: true });
  };

  shouldOutsideDateWindowInfoBeDisplayed = () => {
    const visit = this.props.visit;

    const isOutsideDateWindowInformationEnabled = this.props.isOutsideDateWindowInformationEnabled?.["value"];

    const currentVisitDate = new Date(visit.startDate);
    currentVisitDate.setHours(0, 0, 0, 0);

    const originalVisitDate = visit.visitAttributes?.[ORIGINAL_VISIT_DATE_ATTRIBUTE_TYPE_NAME];
    const originalVisitDateObject = moment(originalVisitDate, "YYYY-MM-DD HH:mm:ss").toDate();
    originalVisitDateObject.setHours(0, 0, 0, 0);

    const lowWindow = parseInt(visit.visitAttributes?.[LOW_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME]);
    const upWindow = parseInt(visit.visitAttributes?.[UP_WINDOW_VISIT_ATTRIBUTE_TYPE_NAME]);
    const lowWindowDate = new Date(originalVisitDateObject.getTime() - lowWindow * ONE_DAY_IN_MILISECONDS);
    const upWindowDate = new Date(originalVisitDateObject.getTime() + upWindow * ONE_DAY_IN_MILISECONDS);

    const isNewVisitDateInRange = currentVisitDate >= lowWindowDate && currentVisitDate <= upWindowDate;

    const dateWindowInfoAvailable = !isNaN(lowWindow) && !isNaN(upWindow);

    const visitTypesWithTimeWindowValue: string = this.props.visitTypesWithTimeWindow!["value"];
    const isVisitTypeHasTimeWindow = visitTypesWithTimeWindowValue
      .split(",")
      .map((value) => value.trim())
      .includes(visit.type);

    return (
      isOutsideDateWindowInformationEnabled === "true" &&
      visit.status === "SCHEDULED" &&
      !isNewVisitDateInRange &&
      dateWindowInfoAvailable &&
      isVisitTypeHasTimeWindow
    );
  };

  renderSaveButton = () => {
    const isExtraInformationEnabled = this.props.isExtraInformationEnabled?.["value"];
    const openModal = isExtraInformationEnabled === "true" || this.shouldOutsideDateWindowInfoBeDisplayed();
    return (
      <Button
        id="schedule-visit-save"
        className="btn btn-success btn-md pull-right confirm"
        onClick={openModal ? this.openExtraInfoModal : this.handleSave}
        disabled={this.state.isSaveButtonDisabled}
      >
        {this.state.saveInProgress ? (
          <i className="icon-spinner icon-spin icon-2x" />
        ) : (
          this.props.intl.formatMessage({
            id: "common.confirm",
            defaultMessage: this.props.intl.formatMessage({ id: "common.confirm" }),
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
    const { errors } = this.props.visit;
    return (
      <Modal
        id="schedule-visit-modal"
        show={show}
        onHide={cancel}
      >
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
    const laterVisits = allVisitDates.filter((date) => date > currentVisitDate);

    if (!laterVisits.length) {
      return null;
    }

    const searchedDate = new Date(Math.min.apply(null, laterVisits));

    return getNumberOfDaysBetweenDates(currentVisitDate, searchedDate);
  };

  findNumberOfDaysBetweenCurrentAndNearestPastVisit = (allVisitDates: Date[], currentVisitDate: Date) => {
    const previousVisits = allVisitDates.filter((date) => date < currentVisitDate);

    if (!previousVisits.length) {
      return null;
    }

    const searchedDate = new Date(Math.max.apply(null, previousVisits));

    return getNumberOfDaysBetweenDates(currentVisitDate, searchedDate);
  };

  isVisitForThisDayDuplicated = (allVisitDates: Date[], currentVisitDate: Date) => {
    return allVisitDates.some((date) => visitDatesTheSame(date, currentVisitDate));
  };

  getPatientVisitsDates = () => {
    let { patientVisits } = this.props;

    if (this.isEdit()) {
      patientVisits = patientVisits.filter(({ uuid }) => uuid != this.props.visitUuid);
    }

    const allPatientVisitsDates = [] as Date[];

    patientVisits.forEach(({ startDatetime }) => allPatientVisitsDates.push(new Date(startDatetime)));

    return allPatientVisitsDates;
  };

  renderExtraInfoModal = () => {
    const { visit, isExtraInformationEnabled, isOutsideDateWindowInformationEnabled, visitTypesWithTimeWindow } =
      this.props;

    if (!isExtraInformationEnabled || !isOutsideDateWindowInformationEnabled || !visitTypesWithTimeWindow) {
      return;
    }

    const currentVisitDate = new Date(visit.startDate);
    currentVisitDate.setHours(0, 0, 0, 0);
    const formattedCurrentVisitDate = formatDateIfDefined(MEDIUM_DATE_FORMAT, currentVisitDate);

    const closedClinicWeekdays = visit?.locationAttributeDTOS?.find(
      (locationDTO) => locationDTO?.locationUuid === visit.location,
    )?.locationAttributesMap?.[CLINIC_CLOSED_WEEKDAYS_ATTRIBUTE_TYPE_UUID];
    const currentVisitWeekday = currentVisitDate.toLocaleDateString("en-us", { weekday: "long" });
    const isClosedClinicWeekday = closedClinicWeekdays?.split(",").includes(currentVisitWeekday);

    const closedClinicDates = visit?.locationAttributeDTOS?.find(
      (locationDTO) => locationDTO?.locationUuid === visit.location,
    )?.locationAttributesMap?.[CLINIC_CLOSED_DATES_ATTRIBUTE_TYPE_UUID];
    const isClosedClinicDate = closedClinicDates?.split(",").includes(formattedCurrentVisitDate);
    const isClosedClinic = isClosedClinicWeekday || isClosedClinicDate;

    const patientVisitsDates = this.getPatientVisitsDates();

    const modalParams = {
      isExtraInformationEnabled: isExtraInformationEnabled?.["value"],
      currentVisitDate: formattedCurrentVisitDate,
      currentVisitWeekday,
      precedingVisitDaysNumber: this.findNumberOfDaysBetweenCurrentAndNearestPastVisit(
        patientVisitsDates,
        currentVisitDate,
      ),
      nextVistitDaysNumber: this.findNumberOfDaysBetweenCurrentAndNearestFutureVisit(
        patientVisitsDates,
        currentVisitDate,
      ),
      isClosedClinic,
      isVisitDateDuplicated: this.isVisitForThisDayDuplicated(patientVisitsDates, currentVisitDate),
      shouldOutsideDateWindowInfoBeDisplayed: this.shouldOutsideDateWindowInfoBeDisplayed(),
    };

    return (
      <ExtraInformationModal
        show={this.state.showExtraInfoModal}
        modalParams={modalParams}
        confirm={this.confirmSavingVisitOnExtraInfoModal}
        cancel={this.closeExtraInfoModal}
        locale={this.props.locale}
      />
    );
  };

  confirmSavingVisitOnExtraInfoModal = () => {
    this.handleSave();
  };

  closeExtraInfoModal = () => {
    this.setState({ showExtraInfoModal: false });
  };

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

const mapStateToProps = ({ scheduleVisit, globalPropertyReducer, session, overview }: IRootState) => ({
  sessionLocation: session.session.sessionLocation,
  visit: scheduleVisit.visit,
  visitTypes: scheduleVisit.visitTypes,
  visitStatuses: scheduleVisit.visitStatuses,
  visitTimes: scheduleVisit.visitTimes,
  locations: scheduleVisit.locations,
  patientVisits: overview.visits,
  isExtraInformationEnabled: globalPropertyReducer.isExtraInfoModalEnabled,
  isOutsideDateWindowInformationEnabled: globalPropertyReducer.isOutsideDateWindowModalEnabled,
  visitTypesWithTimeWindow: globalPropertyReducer.visitTypesWithTimeWindow,
});

const mapDispatchToProps = {
  getSession,
  getVisitTypes,
  getVisitTimes,
  getLocations,
  updateVisit,
  saveVisit,
  getVisit,
  getVisitStatuses,
  reset,
  getExtraInfoModalEnabledGP,
  getOutsideDateWindowInfoModalEnabledGP,
  getVisitTypeUuidsWithTimeWindowGP,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(mapStateToProps, mapDispatchToProps)(ScheduleVisitModal));
