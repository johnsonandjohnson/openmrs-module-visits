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
import { Form, FormGroup, Input } from 'reactstrap';
import { history } from '../../config/redux-store';
import _ from 'lodash';
// TODO: Once AGRE-1584 is developed, date range filter
// among other additional filters will be delivered in AGRE-1593
//import "react-dates/initialize";
//import "react-dates/lib/css/_datepicker.css";
import 'bootstrap/dist/css/bootstrap.min.css';

import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import {
  getOverviewPage,
  updateSearch,
  getLocation
} from '../../reducers/overview-visits.reducer';
import { formatDateIfDefined } from '../../shared/utils/date-util';
import { IRootState } from '../../reducers';
import './index.scss';
import OverviewVisitTable from "./table";
import { SINGLE_PAGE_NUMBER } from './table/constants';
// TODO: Once AGRE-1584 is developed, date range filter
// among other additional filters will be delivered in AGRE-1593
//import { DateRangePicker } from "react-dates";
//import moment from 'moment';

const IDENTIFIER_ACCESSOR = 'patientIdentifier';
const NAME_URL_ACCESSOR = 'nameUrl';
const START_DATE_ACCESSOR = 'startDate';
const ACTUAL_DATE_ACCESSOR = 'actualDate';
const TIME_ACCESSOR = 'time';
const TYPE_ACCESSOR = 'type';
const STATUS_ACCESSOR = 'status';
const OVERVIEW_DATE_FORMAT = 'DD MMM YYYY';
const SEARCH_INPUT_DELAY = 500;
const SEARCH_INPUT_MIN_CHARS = 3;

const searchIcon = require('../../../img/search.png');

interface IProps extends DispatchProps, StateProps { }

interface IState {
  query: string;
  // TODO: Once AGRE-1584 is developed, date range filter
  // among other additional filters will be delivered in AGRE-1593
  //startDate: moment.Moment,
  //endDate: moment.Moment,
  //focusedDatePicker: any
}

class OverviewVisits extends React.Component<IProps, IState> {
  state = {
    query: '',
    // TODO: Once AGRE-1584 is developed, date range filter
    // among other additional filters will be delivered in AGRE-1593
    //startDate: moment(),
    //endDate: moment(),
    //focusedDatePicker: null
  };

  private getVisits = (activePage: number, itemsPerPage: number, sort: string, order: string, filters: {}, query?: string) => {
    if (!!this.props.locationUuid) {
      this.props.getOverviewPage(activePage,
                                  itemsPerPage,
                                  this.props.locationUuid,
                                  query);
    } else {
      this.props.getLocation(activePage, itemsPerPage, query);
    }
  }

  private handleShowPatient = () => {
    history.push(`/visits/overview`);
  }

  private getNameCell = () => {
    return {
      Header: getIntl().formatMessage({ id: 'VISITS_OVERVIEW_NAME_HEADER', defaultMessage: Default.OVERVIEW_NAME_HEADER }),
      accessor: NAME_URL_ACCESSOR,
      Cell: props => {
        return (
          <div
            className="td-cell"
            onClick={this.handleShowPatient}>
            <div className="td-cell">{props.value.name}</div>
          </div>);
      }
    };
  }

  private getIdCell = () => {
    return {
      Header: getIntl().formatMessage({ id: 'VISITS_OVERVIEW_PATIENT_ID_HEADER', defaultMessage: Default.OVERVIEW_PATIENT_ID_HEADER }),
      accessor: IDENTIFIER_ACCESSOR,
      Cell: props => {
        return <div className="td-cell">{props.value}</div>;
      }
    };
  }

  private getCell = (header: string, accessor: string) => {
    return {
      Header: header,
      accessor: accessor,
      Cell: props => {
        return <div className="td-cell">{props.value}</div>;
      }
    };
  }

  private onRowClick = (visit: {}) => {
    const patientName = visit[NAME_URL_ACCESSOR];
    if (!!patientName && !!patientName.url) {
      window.location.assign(patientName.url);
    }
  }

  private searchAfterDelay = _.debounce(() => {
    if (this.state.query.length >= SEARCH_INPUT_MIN_CHARS) {
      this.props.updateSearch(this.state.query);
    }
  }, SEARCH_INPUT_DELAY);

  private search = () => {
    if (this.state.query.length >= SEARCH_INPUT_MIN_CHARS) {
      this.props.updateSearch(this.state.query);
    }
  };

  private onQueryChange = (event: any) => {
    this.setState(
      { query: event.target.value },
      () => this.searchAfterDelay()
    );
  };

  private onSearchClick = (event: any) => {
    event.preventDefault();
    this.searchAfterDelay.cancel();
    this.search();
  };

  private helperText = (query: string, loading: boolean, totalCount: number) => {
    if (query.length < SEARCH_INPUT_MIN_CHARS) {
      return getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TABLE_ENTER_SEARCH' });
    } else if (totalCount > 0) {
      return (
        <span>
          {totalCount} {getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TABLE_RECORDS_FOUND' })}
        </span>
      );
    } else if (!loading && totalCount === 0) {
      return getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TABLE_NO_RECORDS' });
    }
  };

  private renderSearchBar = () => {
    return (
      <>
        <Form onSubmit={this.onSearchClick}>
          <FormGroup className="visit-search">
            <div className="search-bar">
              <img src={searchIcon} alt="search" className="search-icon" />
              <Input
                placeholder={getIntl().formatMessage({
                  id: 'VISITS_OVERVIEW_SEARCH_PLACEHOLDER'
                })}
                value={this.state.query}
                onChange={this.onQueryChange}
                className="search-input"
              />
            </div>
            {// TODO: Once AGRE-1584 is developed, date range filter
            // among other additional filters will be delivered in AGRE-1593
            /*
            <DateRangePicker
              startDate={this.state.startDate}
              startDateId="start_planned_date_id"
              endDate={this.state.endDate}
              endDateId="end_planned_date_id"
              onDatesChange={({ startDate, endDate }) => this.setState({ startDate, endDate })}
              focusedInput={this.state.focusedDatePicker}
              onFocusChange={focusedDatePicker => this.setState({ focusedDatePicker })}
              regular
              showClearDates
              displayFormat={OVERVIEW_DATE_FORMAT}
            />
            */}
          </FormGroup>
        </Form>
      </>
    );
  }

  private renderTable = () => {
    return (
      <div className="visit-table">
        <div className="helper-text">
          {this.props.loading ? (
            <div className="spinner-border spinner-border-sm" />
          ) : (
            this.helperText(this.state.query, this.props.loading, this.props.visits.length)
          )}
        </div>
        {this.state.query.length >= SEARCH_INPUT_MIN_CHARS && (
          <OverviewVisitTable
            data={this.props.visits.map(visit => {
              return {
                ...visit,
                startDate: formatDateIfDefined(OVERVIEW_DATE_FORMAT, visit.startDate),
                actualDate: formatDateIfDefined(OVERVIEW_DATE_FORMAT, visit.actualDate)
              }
            })}
            columns={[
              this.getIdCell(),
              this.getNameCell(),
              this.getCell(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TYPE_HEADER', defaultMessage: Default.OVERVIEW_TYPE_HEADER }), TYPE_ACCESSOR),
              this.getCell(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TIME_HEADER', defaultMessage: Default.OVERVIEW_TIME_HEADER }), TIME_ACCESSOR),
              this.getCell(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_DATE_HEADER', defaultMessage: Default.OVERVIEW_DATE_HEADER }), START_DATE_ACCESSOR),
              this.getCell(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_ACTUAL_DATE_HEADER', defaultMessage: Default.OVERVIEW_ACTUAL_DATE_HEADER }), ACTUAL_DATE_ACCESSOR),
              this.getCell(getIntl().formatMessage({ id: 'VISITS_OVERVIEW_STATUS_HEADER', defaultMessage: Default.OVERVIEW_STATUS_HEADER }), STATUS_ACCESSOR)
            ]}
            query={this.props.search}
            pages={this.props.pages}
            loading={this.props.loading}
            fetchDataCallback={this.getVisits}
            sortable={false}
            multiSort={false}
            showPagination={this.props.pages > SINGLE_PAGE_NUMBER}
            resizable={false}
            onRowClick={this.onRowClick}
          />
        )}
      </div>
    );
  }

  render = () => {
    return (
      <div className="body-wrapper">
        <div className="content">
          <div className="overview-visits">
            <h2>{getIntl().formatMessage({ id: 'VISITS_OVERVIEW_TITLE', defaultMessage: Default.OVERVIEW_TITLE })}</h2>
            <div className="helper-text">
              {getIntl().formatMessage({ id: 'VISITS_OVERVIEW_DESCRIPTION', defaultMessage: Default.OVERVIEW_DESCRIPTION })}
            </div>
            <div className="search-section">
              {this.renderSearchBar()}
              {this.renderTable()}
            </div>
          </div>
        </div>
      </div>
    );
  };
}

const mapStateToProps = ({ overview, openmrs }: IRootState) => ({
  visits: overview.visits,
  pages: overview.pages,
  loading: overview.loading,
  search: overview.search,
  locationUuid: overview.locationUuid
  // ToDo: When CFLM-626 will be fixed please use
  // location: openmrs.session.sessionLocation
  // instead of locationUuid: overview.locationUuid
});

const mapDispatchToProps = ({
  getOverviewPage,
  updateSearch,
  getLocation
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OverviewVisits);
