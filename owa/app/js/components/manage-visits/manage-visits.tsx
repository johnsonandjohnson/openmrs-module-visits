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
import { Form, ControlLabel, FormGroup, Button } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import Table from '@bit/soldevelo-omrs.cfl-components.table/table';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import _ from 'lodash';

import { getVisitsPage, getVisitsPagesCount } from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import {
  MANAGE_VISITS,
  SCHEDULE_VISIT,
  MANAGE_VISITS_COLUMNS,
  ACTIONS_COLUMN_LABEL
} from '../../shared/utils/messages';
import { history } from '../../config/redux-store';
import './manage-visits.scss';
import moment from 'moment';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string
}> { }

interface IState {
}

class ManageVisits extends React.Component<IProps, IState> {

  componentDidMount() {
  }

  private getVisits = (activePage: number, itemsPerPage: number, sort: string, order: string, filters: {}) => {
    this.props.getVisitsPage(activePage, itemsPerPage, this.props.match.params.patientUuid);
  }

  private handleScheduleVisitButton = () => {
    history.push(`/visits/manage/${this.props.match.params.patientUuid}/schedule`);
  }

  private getActionsColumn = () => {
    return {
      Header: ACTIONS_COLUMN_LABEL,
      accessor: 'uuid',
      getProps: () => {
        return {
          style: {
            maxWidth: 30,
            textAlign: 'center',
            margin: 'auto'
          },
        };
      },
      Cell: props => {
        const editLink = `#${this.props.location.pathname}/schedule/${props.value}`;
        return (
          <span>
            <a href={editLink}>
              <FontAwesomeIcon icon={['fas', 'pencil-alt']} size="1x" />
            </a>
          </span>
        );
      }
    };
  }

  private renderScheduleVisitButton = () => {
    return (
      <Button
        className="btn btn-success btn-md"
        onClick={this.handleScheduleVisitButton}>
        {SCHEDULE_VISIT}
      </Button>
    );
  }

  private renderTable = () =>
    <Table
      data={this.props.visits.map(visit => {
        return {
        ...visit,
        startDate: visit.startDate ? moment(visit.startDate).format("DD.MM.YYYY") : visit.startDate
      }})}
      columns={[
        ...MANAGE_VISITS_COLUMNS,
        this.getActionsColumn()
      ]}
      loading={this.props.loading}
      pages={this.props.visitsPagesCount}
      fetchDataCallback={this.getVisits}
      sortable={false}
      multiSort={false}
      showPagination={true}
    />

  render() {
    return (
      <div className="manage-visits">
        <Form className="fields-form">
          <ControlLabel className="fields-form-title">
            <h2>{MANAGE_VISITS}</h2>
          </ControlLabel>
          <div className="button-section">
            {this.renderScheduleVisitButton()}
          </div>
          <FormGroup>
            {this.renderTable()}
          </FormGroup>
        </Form>
      </div>
    );
  };
}

const mapStateToProps = ({ scheduleVisit }: IRootState) => ({
  visits: scheduleVisit.visits,
  visitsPagesCount: scheduleVisit.visitsPagesCount,
  loading: scheduleVisit.visitsLoading || scheduleVisit.visitsPagesCount === 0,
});

const mapDispatchToProps = ({
  getVisitsPage,
  getVisitsPagesCount
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ManageVisits);
