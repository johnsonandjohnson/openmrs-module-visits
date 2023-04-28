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
import { PropsWithIntl } from '../../components/translation/PropsWithIntl';
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
import { injectIntl } from 'react-intl';

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

interface IProps extends DispatchProps, StateProps {

}

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
  isAllVisitsChecked: boolean;
  visitStatusToUpdate: string;
}

class OverviewVisits extends React.Component<PropsWithIntl<IProps>, IState> {
  state = {
    query: "",
    filters: {
      dateFrom: null,
      dateTo: null,
      visitStatus: {
        label: SCHEDULED_STATUS,
        value: SCHEDULED_STATUS,
      },
      timePeriod: {
        label: DEFAULT_TIME_PERIOD,
        value: DEFAULT_TIME_PERIOD,
      },
    },
    focusedDatePicker: null,
    showChangeVisitsStatusesModal: false,
    modalParams: null as IChangeStatusesModalParams | null,
    saveButtonDisabled: true,
    selectedVisitsUuids: [] as string[],
    isAllVisitsChecked: false,
    visitStatusToUpdate: ''
  };

  componentGetTranslatedLabel() {
    const { filters } = this.state;
    return { ...filters.timePeriod, 
      label: this.props.intl.formatMessage({  
        id: `visits.overviewPredefinedFilters${filters.timePeriod.value.toUpperCase()}Label`}) };
  }

  componentDidMount() {
    this.props.getVisitStatuses();
  }
  
  componentDidUpdate(prevProps, prevState) {
    const locationUuid = this.props.location?.uuid;
    const prevLocationUuid = prevProps.location?.uuid;
    const { filters, query } = this.state
    const { isVisitStatusesUpdateSuccess } = this.props;

    if (filters.timePeriod.value === DEFAULT_TIME_PERIOD && filters.dateFrom == null && filters.dateTo == null) {
      this.updateFilters();
    }

    if (filters.timePeriod.value !== prevState.filters.timePeriod.value) {
      this.updateFilters();
    }

    if ((locationUuid !== prevLocationUuid) || (isVisitStatusesUpdateSuccess && !prevProps.isVisitStatusesUpdateSuccess)) {
      this.getVisits(DEFAULT_ACTIVE_PAGE, DEFAULT_ITEMS_PER_PAGE, DEFAULT_SORT, DEFAULT_ORDER, filters, query);
    }
  }

  updateFilters() {
    const { filters } = this.state
    const { dateFrom, dateTo } = getDatesByPeriod[filters.timePeriod.value]();
    this.setState({
      filters: {
        ...filters,
        dateFrom: dateFrom ? dateFrom : filters.dateFrom,
        dateTo: dateTo ? dateTo : filters.dateTo
      }
    });
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
      Cell: ({ value }) => {
        return (
          <div>
            <input id={value} type="checkbox" className="checkbox-field" 
              checked={this.state.selectedVisitsUuids.includes(value)} 
              onClick={this.handleOnClickVisitCheckbox}/>
          </div>  
        );
      }
    };
  };

  private handleOnClickVisitCheckbox = e => {
    e.stopPropagation();

    const checkBoxElement = e.target;
    const visitUuid = checkBoxElement.id;
    let selectedVisitsUuids = this.state.selectedVisitsUuids;
    let searchUuid = selectedVisitsUuids.indexOf(visitUuid);

    if (searchUuid > -1) {
      selectedVisitsUuids.splice(searchUuid, 1);
    } else {
      selectedVisitsUuids.push(visitUuid);
    }

    this.setState({ selectedVisitsUuids })
 
    const isAnyVisitSelected = this.state.selectedVisitsUuids.length > 0;
    const visitStatus = this.state.visitStatusToUpdate;
    if (isAnyVisitSelected && visitStatus) {
      this.setState({ saveButtonDisabled: false })
    } else {
      this.setState({ saveButtonDisabled: true })
    }
  }

  private getNameCell = () => {
    return {
      Header: this.props.intl.formatMessage({
        id: "visits.overviewNameHeader"
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
      Header: this.props.intl.formatMessage({
        id: "visits.overviewPatientIdHeader"
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
          {totalCount} {this.props.intl.formatMessage({ id: "visits.overviewTableRecordsFound" })}
        </span>
      );
    } else if (!loading && totalCount === 0) {
      return this.props.intl.formatMessage({ id: "visits.overviewTableNoRecords" });
    }
  };

  private timePeriodOptions = () =>
    TIME_PERIOD_OPTIONS.map((timePeriod) => ({
      label: this.props.intl.formatMessage({
        id: `visits.overviewPredefinedFilters${timePeriod.toUpperCase()}Label`,
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
      placeholder={this.props.intl.formatMessage({
        id: "overviewPredefinedFiltersPlaceholder"
      })}
      value={this.componentGetTranslatedLabel()}
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
      placeholder={this.props.intl.formatMessage({
        id: "visits.overviewVisitStatusPlaceholder"
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
      onChange={option => this.visitStatusToUpdateOnChange(option)}
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

  visitStatusToUpdateOnChange = option => {
    let saveButtonDisabled = true;
    let visitStatusToUpdate = '';
    
    if (option) {
      visitStatusToUpdate = option.value;
      const isAnyCheckboxSelected = this.state.selectedVisitsUuids.length > 0;
      if (isAnyCheckboxSelected) {
        saveButtonDisabled = false;
      }
    }

    this.setState({ saveButtonDisabled, visitStatusToUpdate })
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
        startDatePlaceholderText={this.props.intl.formatMessage({ id: 'visits.overviewStartDate' })}
        endDate={dateTo}
        endDateId="date_to"
        endDatePlaceholderText={this.props.intl.formatMessage({ id: 'visits.overviewEndDate' })}
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
                placeholder={this.props.intl.formatMessage({
                  id: "visits.overviewSearchPlaceholder"
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

  renderChangeVisitsStatusesModal = () => (
    <ChangeVisitsStatusesModal 
      show={this.state.showChangeVisitsStatusesModal}
      modalParams={this.state.modalParams}
      confirm={this.confirmChangeStatuses}
      cancel={this.closeChangeVisitsStatusesModal}
    />
  );

  confirmChangeStatuses = (modalParams: IChangeStatusesModalParams | null) => {
    if (modalParams) {
      const { visitsUuids, newVisitStatus } = modalParams;
      this.props.updateVisitStatuses(visitsUuids, newVisitStatus, this.props.intl);
      this.closeChangeVisitsStatusesModal();
      this.clearCheckboxes();
    }
  };

  closeChangeVisitsStatusesModal = () => this.setState({ showChangeVisitsStatusesModal: false });

  clearCheckboxes = () => this.setState({ selectedVisitsUuids: [], isAllVisitsChecked: false});

  renderSaveButton = () => (
    <Button
      className="btn btn-success btn-md add-btn"
      onClick={this.handleSaveButton}
      disabled={this.state.saveButtonDisabled}
    >
      {this.props.intl.formatMessage({ id: "visits.overviewSaveButtonLabel" })}
    </Button>
  );

  handleSaveButton = () => {
    const visitUuids = this.state.selectedVisitsUuids;
    const visitStatus = this.state.visitStatusToUpdate;
    const modalParams: IChangeStatusesModalParams = createChangeStatusesModalParams(visitUuids, visitStatus);
    
    this.openChangeStatusesDialog(modalParams);
  }

  private openChangeStatusesDialog = (modalParams: IChangeStatusesModalParams) => {
    if (modalParams.visitsUuids.length && modalParams.newVisitStatus) {
      this.setState({showChangeVisitsStatusesModal: true, modalParams });
    }
  }

  handleSelectAll = e => {
    const selectAllCheckbox = e.target as HTMLInputElement;
    const isSelectAllCheckboxChecked = selectAllCheckbox.checked;
    
    let isAllVisitsChecked = false;
    let visitsUuids: string[] = [];
    let saveButtonDisabled = true;
    
    if (isSelectAllCheckboxChecked) {
      isAllVisitsChecked = true;
      visitsUuids = this.props.visits.map(({ uuid }) => uuid);
      const visitStatus = this.state.visitStatusToUpdate;
      if (visitStatus) {
        saveButtonDisabled = false;
      }
    }

    this.setState({ selectedVisitsUuids: visitsUuids, saveButtonDisabled, isAllVisitsChecked })
  }

  private renderChangeVisitsStatusesSection = () => {
    return (
      <Form>
        <FormGroup className="change-visits-statuses-form-group">
          <div className="select-all-section">
            <input type="checkbox" id="selectAllCheckbox" onClick={this.handleSelectAll} checked={this.state.isAllVisitsChecked}/>
            <span id="selectAllSpan">{this.props.intl.formatMessage({ id: "visits.overviewSelectAllHeader" })}</span>
          </div>
          <div className="change-visits-statuses-select-section-parent">
            <div className="change-visits-statuses-right-section">
              <div className="set-selected-visits-div">
                <span>{this.props.intl.formatMessage({ id: "visits.overviewSetSelectedVisitsHeader" })}</span>
              </div>
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
            this.helperText(this.props.loading, this.props.totalCount)
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
              this.props.intl.formatMessage({
                id: "visits.overviewTypeHeader"
              }),
              TYPE_ACCESSOR
            ),
            this.getCell(
              this.props.intl.formatMessage({
                id: "visits.overviewTimeHeader"
              }),
              TIME_ACCESSOR
            ),
            this.getCell(
              this.props.intl.formatMessage({
                id: "visits.overviewDateHeader"
              }),
              START_DATE_ACCESSOR
            ),
            this.getCell(
              this.props.intl.formatMessage({
                id: "visits.overviewActualDateHeader"
              }),
              ACTUAL_DATE_ACCESSOR
            ),
            this.getCell(
              this.props.intl.formatMessage({
                id: "visits.overviewStatusHeader"
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
          intl={this.props.intl}        />
      </div>
    );
  };

  render = () => {
    return (
      <div className="body-wrapper">
        <div className="content">
          <div className="overview-visits">
            <h2>{this.props.intl.formatMessage({ id: "visits.overviewTittle" })}</h2>
            <div className="helper-text">
              {this.props.intl.formatMessage({
                id: "visits.overviewDescription"
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
  totalCount: overview.totalCount,
  loading: overview.loading,
  location: openmrs.session.sessionLocation,
  visitStatuses: scheduleVisit.visitStatuses,
  isVisitStatusesUpdateSuccess: overview.isVisitStatusesUpdateSuccess
});

const mapDispatchToProps = {
  getOverviewPage,
  getVisitStatuses,
  updateVisitStatuses,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(mapStateToProps, mapDispatchToProps)(OverviewVisits));