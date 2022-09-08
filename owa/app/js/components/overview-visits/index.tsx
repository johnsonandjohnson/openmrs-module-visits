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
import { Form, FormGroup, Input } from "reactstrap";
import Select from "react-select";
import "react-dates/initialize";
import "react-dates/lib/css/_datepicker.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Button } from "react-bootstrap";

import * as Default from "../../shared/utils/messages";
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { getOverviewPage } from "../../reducers/overview-visits.reducer";
import { getVisitStatuses } from "../../reducers/schedule-visit.reducer";
import { updateVisitStatuses } from "../../reducers/overview-visits.reducer";
import { formatDateIfDefined, getDatesByPeriod } from "../../shared/utils/date-util";
import { IRootState } from "../../reducers";
import "./index.scss";
import OverviewVisitTable from "./table";
import {
  SINGLE_PAGE_NUMBER,
  TIME_PERIOD_OPTIONS,
  DEFAULT_ACTIVE_PAGE,
  DEFAULT_ITEMS_PER_PAGE,
  DEFAULT_SORT,
  DEFAULT_ORDER
} from "./table/constants";
import { DateRangePicker } from "react-dates";
import moment from "moment";
import ChangeVisitsStatusesModal from "./change-visits-statuses-modal";
import IChangeStatusesModalParams, { createChangeStatusesModalParams } from "./change-visits-statuses-modal-param";

const IDENTIFIER_ACCESSOR = "patientIdentifier";
const NAME_URL_ACCESSOR = "nameUrl";
const START_DATE_ACCESSOR = "startDate";
const ACTUAL_DATE_ACCESSOR = "actualDate";
const TIME_ACCESSOR = "time";
const TYPE_ACCESSOR = "type";
const STATUS_ACCESSOR = "status";
const OVERVIEW_DATE_FORMAT = "DD MMM YYYY";
const DAY = "day";
const MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH = 768;
const DEFAULT_TIME_PERIOD = "TODAY";
const SCHEDULED_STATUS = "SCHEDULED";
const TIME_PERIOD_ALL = "ALL";

const searchIcon = require("../../../img/search.png");

interface IProps extends DispatchProps, StateProps {}

interface IFilters {
  dateFrom: moment.Moment | null;
  dateTo: moment.Moment | null;
  visitStatus: {
    label: string;
    value: string;
  } | null;
  timePeriod: {
    label: string;
    value: string;
  } | null;
}

interface IState {
  query: string;
  filters: IFilters;
  focusedDatePicker: any;
  showChangeVisitsStatusesModal: boolean;
  modalParams: IChangeStatusesModalParams | null;
  saveButtonDisabled: boolean;
  selectedVisitsUuids: string[];
}

class OverviewVisits extends React.Component<IProps, IState> {
  state = {
    query: "",
    filters: {
      dateFrom: moment(),
      dateTo: moment(),
      visitStatus: {
        label: SCHEDULED_STATUS,
        value: SCHEDULED_STATUS,
      },
      timePeriod: {
        label: getIntl().formatMessage({
          id: `VISITS_OVERVIEW_PREDEFINED_FILTERS_${DEFAULT_TIME_PERIOD}_LABEL`,
        }),
        value: DEFAULT_TIME_PERIOD,
      },
    },
    focusedDatePicker: null,
    showChangeVisitsStatusesModal: false,
    modalParams: null as IChangeStatusesModalParams | null,
    saveButtonDisabled: true,
    selectedVisitsUuids: [] as string[]
  };

  componentDidMount() {
    this.props.getVisitStatuses();
  }

  componentDidUpdate(prevProps) {
    const locationUuid = this.props.location?.uuid;
    const prevLocationUuid = prevProps.location?.uuid;
    const { filters, query } = this.state
    
    if (locationUuid !== prevLocationUuid) {
      this.getVisits(DEFAULT_ACTIVE_PAGE, DEFAULT_ITEMS_PER_PAGE, DEFAULT_SORT, DEFAULT_ORDER, filters, query);
    }
  }

  private getVisits = (
    activePage: number,
    itemsPerPage: number,
    sort: string,
    order: string,
    filters?: IFilters,
    query?: string
  ) => {
    const timePeriod = filters?.timePeriod?.value;
    const isTimePeriodAll = timePeriod === TIME_PERIOD_ALL;
    const dateFrom = isTimePeriodAll ? filters?.dateFrom?.startOf(DAY).valueOf() : null ;
    const dateTo = isTimePeriodAll ? filters?.dateTo?.endOf(DAY).valueOf() : null;
    const predefinedFilters = {
      dateFrom,
      dateTo,
      visitStatus: filters?.visitStatus?.value,
      timePeriod,
    };
    const locationUuid = this.props.location?.uuid;

    if (locationUuid) {
      this.props.getOverviewPage(activePage, itemsPerPage, locationUuid, predefinedFilters, query);
    } 
  };

  private getCheckboxCell = () => {
    return {
      width: 25,
      accessor: "uuid",
      Cell: (props) => {
        return (
          <div>
            <input id={props.value} type="checkbox" className="checkbox-field" 
              checked={this.state.selectedVisitsUuids.includes(props.value)} 
              onClick={this.handleOnClickVisitCheckbox}/>
          </div>  
        );
      }
    };
  };

  private handleOnClickVisitCheckbox = (e) => {
    e.stopPropagation();

    const checkBoxElement = e.target;
    const visitUuid = checkBoxElement.id;
    let selections = this.state.selectedVisitsUuids;
    let searchUuid = selections.indexOf(visitUuid);

    if (searchUuid > -1) {
      selections.splice(searchUuid, 1);
    } else {
      selections.push(visitUuid);
    }

    this.setState({ selectedVisitsUuids: selections })
 
    const isAnyVisitSelected = this.state.selectedVisitsUuids.length > 0;
    const visitStatus = this.getVisitStatusToUpdate();
    if (isAnyVisitSelected && !!visitStatus) {
      this.setState({ saveButtonDisabled: false })
    } else {
      this.setState({ saveButtonDisabled: true })
    }
  }

  private getSelectedVisitsUuids = () => {
    const checkboxesList = document.querySelectorAll('.checkbox-field');
    
    let visitUuids : string[] = [];
    checkboxesList.forEach(function(checkbox) {
      const castedElem = checkbox as HTMLInputElement;
      if (castedElem.checked) {
        visitUuids.push(castedElem.id);
      }
    })

    return visitUuids;
  }

  private getVisitStatusToUpdate = () => {
    return document.getElementsByClassName('change-status-select')[0].textContent;
  }

  private getNameCell = () => {
    return {
      Header: getIntl().formatMessage({
        id: "VISITS_OVERVIEW_NAME_HEADER",
        defaultMessage: Default.OVERVIEW_NAME_HEADER,
      }),
      accessor: NAME_URL_ACCESSOR,
      Cell: (props) => {
        return (
          <div className="td-cell">
            <div className="td-cell">{props.value.name}</div>
          </div>
        );
      },
    };
  };

  private getIdCell = () => {
    return {
      Header: getIntl().formatMessage({
        id: "VISITS_OVERVIEW_PATIENT_ID_HEADER",
        defaultMessage: Default.OVERVIEW_PATIENT_ID_HEADER,
      }),
      accessor: IDENTIFIER_ACCESSOR,
      Cell: (props) => {
        return <div className="td-cell">{props.value}</div>;
      },
    };
  };

  private getCell = (header: string, accessor: string) => {
    return {
      Header: header,
      accessor: accessor,
      Cell: (props) => {
        return <div className="td-cell">{props.value}</div>;
      },
    };
  };

  private onRowClick = (visit: {}) => {
    const patientName = visit[NAME_URL_ACCESSOR];
    if (!!patientName && !!patientName.url) {
      window.location.assign(patientName.url);
    }
  };

  private onQueryChange = (event: any) => this.setState({ query: event.target.value });

  private helperText = (loading: boolean, totalCount: number) => {
    if (totalCount > 0) {
      return (
        <span>
          {totalCount} {getIntl().formatMessage({ id: "VISITS_OVERVIEW_TABLE_RECORDS_FOUND" })}
        </span>
      );
    } else if (!loading && totalCount === 0) {
      return getIntl().formatMessage({ id: "VISITS_OVERVIEW_TABLE_NO_RECORDS" });
    }
  };

  private timePeriodOptions = () =>
    TIME_PERIOD_OPTIONS.map((timePeriod) => ({
      label: getIntl().formatMessage({
        id: `VISITS_OVERVIEW_PREDEFINED_FILTERS_${timePeriod.toUpperCase()}_LABEL`,
      }),
      value: timePeriod,
    }));

  private renderTimePeriodSelect = () => (
    <Select
      inputId="visits-overview-filter-period"
      options={this.timePeriodOptions()}
      onChange={(timePeriod) => {
        const { dateFrom, dateTo } = getDatesByPeriod[timePeriod.value]();
        this.setState((state) => ({ filters: { ...state.filters, timePeriod, dateFrom, dateTo} }))
      }}
      className="visits-select"
      classNamePrefix="visits-select"
      placeholder={getIntl().formatMessage({
        id: "VISITS_OVERVIEW_PREDEFINED_FILTERS_PLACEHOLDER",
        defaultMessage: Default.OVERVIEW_PREDEFINED_FILTERS_PLACEHOLDER,
      })}
      value={this.state.filters.timePeriod}
      isSearchable={false}
      theme={theme => ({
        ...theme,
        colors: {
          ...theme.colors,
          primary: "#00455c",
          primary25: "#e4e7e7"
        }
      })}
    />
  );

  private visitStatusOptions = () =>
    this.props.visitStatuses.map((status) => ({
      label: status,
      value: status,
    }));

  private renderVisitStatusSelect = () => (
    <Select
      inputId="visits-overview-filter-status"
      options={this.visitStatusOptions()}
      onChange={(visitStatus) => this.setState((state) => ({ filters: { ...state.filters, visitStatus } }))}
      className="visits-select"
      classNamePrefix="visits-select"
      placeholder={getIntl().formatMessage({
        id: "VISITS_OVERVIEW_VISIT_STATUS_PLACEHOLDER",
        defaultMessage: Default.OVERVIEW_VISIT_STATUS_PLACEHOLDER,
      })}
      value={this.state.filters.visitStatus}
      isSearchable={false}
      isClearable
      theme={theme => ({
        ...theme,
        colors: {
          ...theme.colors,
          primary: "#00455c",
          primary25: "#e4e7e7"
        }
      })}
    />
  );

  private renderVisitStatusesSelectToChange = () => (
    <Select
      options={this.visitStatusOptions()}
      onChange={(option) => this.handleVisitStatusToChangeOnChange(option)}
      className="visits-select change-status-select"
      classNamePrefix="visits-select"
      placeholder=""
      isSearchable={false}
      isClearable
      theme={theme => ({
        ...theme,
        colors: {
          ...theme.colors,
          primary: "#00455c",
          primary25: "#e4e7e7"
        }
      })}
    />
  );

  handleVisitStatusToChangeOnChange = (option) => {
    if (option == null) {
      this.setState({ saveButtonDisabled: true })
    } else {
      const isAnyCheckboxSelected = this.state.selectedVisitsUuids.length > 0;
      if (isAnyCheckboxSelected) {
        this.setState({ saveButtonDisabled: false })
      }
    }
  }

  private renderDateRangePicker = () => {
    const {
      focusedDatePicker,
      filters: {
        timePeriod: { value: timePeriodValue },
        dateFrom,
        dateTo,
      },
    } = this.state;

    return (
      <DateRangePicker
        startDate={dateFrom}
        startDateId="date_from"
        endDate={dateTo}
        endDateId="date_to"
        onDatesChange={({ startDate, endDate }) =>
          this.setState((state) => ({ filters: { ...state.filters, dateFrom: startDate, dateTo: endDate } }))
        }
        focusedInput={focusedDatePicker}
        onFocusChange={(focusedDatePicker) => this.setState({ focusedDatePicker })}
        showClearDates={timePeriodValue === TIME_PERIOD_ALL}
        displayFormat={OVERVIEW_DATE_FORMAT}
        hideKeyboardShortcutsPanel
        isOutsideRange={() => false}
        showDefaultInputIcon
        orientation={window.screen.availWidth > MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH ? "horizontal" : "vertical"}
        small={window.screen.availWidth < MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH}
        disabled={timePeriodValue !== TIME_PERIOD_ALL}
      />
    )
  };

  private renderSearchBar = () => {
    return (
      <>
        <Form>
          <FormGroup className="visit-search">
            <div className="search-bar">
              <img src={searchIcon} alt="search" className="search-icon" />
              <Input
                id="visits-overview-filter-name"
                placeholder={getIntl().formatMessage({
                  id: "VISITS_OVERVIEW_SEARCH_PLACEHOLDER",
                })}
                value={this.state.query}
                onChange={this.onQueryChange}
                className="search-input form-control"
              />
            </div>
            {this.renderDateRangePicker()}
            {this.renderVisitStatusSelect()}
            {this.renderTimePeriodSelect()}
          </FormGroup>
        </Form>
      </>
    );
  };

  renderChangeVisitsStatusesModal = () => {
    return (
      <ChangeVisitsStatusesModal 
        show={this.state.showChangeVisitsStatusesModal}
        modalParams={this.state.modalParams}
        confirm={this.confirmChangeStatuses}
        cancel={this.closeChangeVisitsStatusesModal}
      />
    );
  }

  confirmChangeStatuses = (modalParams: IChangeStatusesModalParams | null) => {
    if (!!modalParams) {
      const { visitsUuids, newVisitStatus } = modalParams;
      this.props.updateVisitStatuses(visitsUuids, newVisitStatus);
      this.closeChangeVisitsStatusesModal();
      window.location.reload();
    }
  };

  closeChangeVisitsStatusesModal = () => {
    this.setState({ showChangeVisitsStatusesModal: false });
  }

  renderSaveButton() {
    return (
      <Button
        className="btn btn-success btn-md add-btn"
        onClick={this.handleSaveButton}
        disabled={this.state.saveButtonDisabled}
      >
        {getIntl().formatMessage({ id: 'VISITS_OVERVIEW_SAVE_BUTTON_LABEL', defaultMessage: Default.SAVE_BUTTON_LABEL })}
      </Button>
    );
  }

  handleSaveButton = () => {
    const visitUuids = this.getSelectedVisitsUuids();
    const visitStatus = this.getVisitStatusToUpdate();
    const modalParams: IChangeStatusesModalParams = createChangeStatusesModalParams(visitUuids, visitStatus);
    
    this.openChangeStatusesDialog(modalParams);
  }

  private openChangeStatusesDialog = (modalParams: IChangeStatusesModalParams) => {
    if (modalParams.visitsUuids.length > 0 && !!modalParams.newVisitStatus) {
      this.setState({showChangeVisitsStatusesModal: true, modalParams });
    }
  }

  handleSelectAll = () => {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox') as HTMLInputElement;
    const checkboxesList = document.querySelectorAll('.checkbox-field');
    
    let visitUuids: string[] = [];
    let saveButtonDisabled = true;
    if (selectAllCheckbox.checked) {
      checkboxesList.forEach(function(checbkox) {
        const castedElem = checbkox as HTMLInputElement;
        visitUuids.push(castedElem.id);
      });

      const visitStatus = this.getVisitStatusToUpdate();
      const isAnyCheckboxSelected = visitUuids.length > 0;
      if (isAnyCheckboxSelected && !!visitStatus) {
        saveButtonDisabled = false;
      }
    }

    this.setState({ selectedVisitsUuids: visitUuids, saveButtonDisabled: saveButtonDisabled})
  }

  private renderChangeVisitsStatusesSection = () => {
    return (
      <Form>
        <FormGroup className="change-visits-statuses-form-group">
          <div className="select-all-section">
            <input type="checkbox" id="selectAllCheckbox" onClick={this.handleSelectAll}/>
            <span id="selectAllSpan">Select all</span>
          </div>
          <div className="change-visits-statuses-select-section-parent">
            <div className="change-visits-statuses-right-section">
              <div className="set-selected-visits-div"><span>Set selected visits to:</span></div>
              <div className="change-visits-statuses-dropdown-div">
                {this.renderVisitStatusesSelectToChange()}
              </div>
              <div className="save-button-div">
                {this.renderSaveButton()}
              </div>
            </div>
            {this.renderChangeVisitsStatusesModal()}
          </div>
      </FormGroup>
    </Form>
    );
  };

  private renderTable = () => {
    return (
      <div className="visit-table">
        <div className="helper-text">
          {this.props.loading ? (
            <div className="spinner-border spinner-border-sm" />
          ) : (
            this.helperText(this.props.loading, this.props.visits.length)
          )}
        </div>
        <OverviewVisitTable
          data={this.props.visits.map((visit) => {
            return {
              ...visit,
              startDate: formatDateIfDefined(OVERVIEW_DATE_FORMAT, visit.startDate),
              actualDate: formatDateIfDefined(OVERVIEW_DATE_FORMAT, visit.actualDate),
            };
          })}
          columns={[
            this.getCheckboxCell(),
            this.getIdCell(),
            this.getNameCell(),
            this.getCell(
              getIntl().formatMessage({
                id: "VISITS_OVERVIEW_TYPE_HEADER",
                defaultMessage: Default.OVERVIEW_TYPE_HEADER,
              }),
              TYPE_ACCESSOR
            ),
            this.getCell(
              getIntl().formatMessage({
                id: "VISITS_OVERVIEW_TIME_HEADER",
                defaultMessage: Default.OVERVIEW_TIME_HEADER,
              }),
              TIME_ACCESSOR
            ),
            this.getCell(
              getIntl().formatMessage({
                id: "VISITS_OVERVIEW_DATE_HEADER",
                defaultMessage: Default.OVERVIEW_DATE_HEADER,
              }),
              START_DATE_ACCESSOR
            ),
            this.getCell(
              getIntl().formatMessage({
                id: "VISITS_OVERVIEW_ACTUAL_DATE_HEADER",
                defaultMessage: Default.OVERVIEW_ACTUAL_DATE_HEADER,
              }),
              ACTUAL_DATE_ACCESSOR
            ),
            this.getCell(
              getIntl().formatMessage({
                id: "VISITS_OVERVIEW_STATUS_HEADER",
                defaultMessage: Default.OVERVIEW_STATUS_HEADER,
              }),
              STATUS_ACCESSOR
            ),
          ]}
          query={this.state.query}
          filters={this.state.filters}
          pages={this.props.pages}
          loading={false}
          fetchDataCallback={this.getVisits}
          sortable={false}
          multiSort={false}
          showPagination={this.props.pages > SINGLE_PAGE_NUMBER}
          resizable={false}
          onRowClick={this.onRowClick}
        />
      </div>
    );
  };

  render = () => {
    return (
      <div className="body-wrapper">
        <div className="content">
          <div className="overview-visits">
            <h2>{getIntl().formatMessage({ id: "VISITS_OVERVIEW_TITLE", defaultMessage: Default.OVERVIEW_TITLE })}</h2>
            <div className="helper-text">
              {getIntl().formatMessage({
                id: "VISITS_OVERVIEW_DESCRIPTION",
                defaultMessage: Default.OVERVIEW_DESCRIPTION,
              })}
            </div>
            <div className="search-section">
              {this.renderSearchBar()}
              {this.renderChangeVisitsStatusesSection()}
              {this.renderTable()}
            </div>
          </div>
        </div>
      </div>
    );
  };
}

const mapStateToProps = ({ overview, scheduleVisit, openmrs }: IRootState) => ({
  visits: overview.visits,
  pages: overview.pages,
  loading: overview.loading,
  location: openmrs.session.sessionLocation,
  visitStatuses: scheduleVisit.visitStatuses,
});

const mapDispatchToProps = {
  getOverviewPage,
  getVisitStatuses,
  updateVisitStatuses,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(OverviewVisits);
