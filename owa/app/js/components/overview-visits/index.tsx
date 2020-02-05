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
import { Form, ControlLabel, FormGroup, Row, Col } from 'react-bootstrap';
import _ from 'lodash';
import Table from '@bit/soldevelo-omrs.cfl-components.table/table';
import { history } from '../../config/redux-store';

import {
  OVERVIEW_TITLE,
  OVERVIEW_PATIENT_ID_HEADER,
  OVERVIEW_DATE_HEADER,
  OVERVIEW_TIME_HEADER,
  OVERVIEW_TYPE_HEADER,
  OVERVIEW_STATUS_HEADER,
  OVERVIEW_NAME_HEADER,
  OVERVIEW_PATIEND_ID_PREFIX,
  OVERVIEW_SEARCH_TITLE,
} from '../../shared/utils/messages';
import {
  getOverviewPage,
  updateSearch,
  getLocation
} from '../../reducers/overview-visits.reducer';
import { formatDateIfDefined } from '../../shared/utils/date-util';
import { IRootState } from '../../reducers';
import './index.scss';

const IDENTIFIER_ACCESSOR = 'patientIdentifier';
const NAME_URL_ACCESSOR = 'nameUrl';
const START_DATE_ACCESSOR = 'startDate';
const TIME_ACCESSOR = 'time';
const TYPE_ACCESSOR = 'type';
const STATUS_ACCESSOR = 'status';
const OVERVIEW_DATE_FORMAT = 'DD.MM.YYYY';

interface IProps extends DispatchProps, StateProps { }

interface IState {
}

class OverviewVisits extends React.Component<IProps, IState> {

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
      Header: OVERVIEW_NAME_HEADER,
      accessor: NAME_URL_ACCESSOR,
      Cell: props => {
        return (
          <div
            className="td-cell"
            onClick={this.handleShowPatient}>
            <a href={props.value.url}>{props.value.name}</a>
          </div>);
      }
    };
  }

  private getIdCell = () => {
    return {
      Header: OVERVIEW_PATIENT_ID_HEADER,
      accessor: IDENTIFIER_ACCESSOR,
      Cell: props => {
        return <div className="td-cell">{OVERVIEW_PATIEND_ID_PREFIX}&nbsp;{props.value}</div>;
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

  private handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    this.props.updateSearch((e.target as HTMLInputElement).value);
  }

  private renderSearchBar = () => {
    return (
      <>
        <Row>
          <Col sm={4}>{OVERVIEW_SEARCH_TITLE}</Col>
        </Row>
        <Row className="search-bar">
          <Col sm={4}>
            <input
              type="text"
              value={this.props.search}
              onChange={(e: any) => this.handleSearch(e)} />
          </Col>
        </Row>
      </>
    );
  }

  private renderTable = () => {
    return (
      <Table
        data={this.props.visits.map(visit => {
          return {
            ...visit,
            startDate: formatDateIfDefined(OVERVIEW_DATE_FORMAT, visit.startDate)
          }
        })}
        columns={[
          this.getIdCell(),
          this.getNameCell(),
          this.getCell(OVERVIEW_DATE_HEADER, START_DATE_ACCESSOR),
          this.getCell(OVERVIEW_TIME_HEADER, TIME_ACCESSOR),
          this.getCell(OVERVIEW_TYPE_HEADER, TYPE_ACCESSOR),
          this.getCell(OVERVIEW_STATUS_HEADER, STATUS_ACCESSOR)
        ]}
        query={this.props.search}
        pages={this.props.pages}
        loading={this.props.loading}
        fetchDataCallback={this.getVisits}
        sortable={false}
        multiSort={false}
        showPagination={true}
      />
    );
  }

  render = () => {
    return (
      <div className="body-wrapper">
        <div className="content">
          <div className="overview-visits">
            <Form className="fields-form">
              <ControlLabel className="fields-form-title">
                <h2>{OVERVIEW_TITLE}</h2>
              </ControlLabel>
              {this.renderSearchBar()}
              <FormGroup>
                {this.renderTable()}
              </FormGroup>
            </Form>
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