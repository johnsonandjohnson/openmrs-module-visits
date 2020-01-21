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
import { getVisits } from '../../reducers/schedule-visit.reducer';
import { IRootState } from '../../reducers';
import { Form, ControlLabel, FormGroup, Button } from 'react-bootstrap';
import { RouteComponentProps } from 'react-router-dom';
import {
  SCHEDULE_VISIT,
  MANAGE_VISITS,
} from '../../shared/utils/messages';
import _ from 'lodash';
import './manage-visits.scss';
import Table from '@bit/soldevelo-omrs.cfl-components.table/table';
import { history } from '../../config/redux-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

interface IProps extends DispatchProps, StateProps, RouteComponentProps<{
  patientUuid: string
}> { }

interface IState {
}

//TODO: CFLM-145: General class refactor
class ManageVisits extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
  }

  componentDidMount() {
    this.props.getVisits(this.props.match.params.patientUuid);
  }

  handleScheduleVisitButton = () => {
    history.push(`/visits/manage/${this.props.match.params.patientUuid}/schedule`);
  }

  renderScheduleVisitButton() {
    return (
      <Button
        className="btn btn-success btn-md"
        onClick={this.handleScheduleVisitButton}>
        {SCHEDULE_VISIT}
      </Button>
    );
  }

  //TODO: CFLM-145: Refactor columns
  renderTable = () => {
    const data = this.props.visits;
    const columns = [
      {
        Header: 'Date',
        accessor: 'visitDate',
      },
      {
        Header: 'Time',
        accessor: 'visitTime',
      },
      {
        Header: 'Location',
        accessor: 'location',
      },
      {
        Header: 'Type',
        accessor: 'visitType',
      },
      {
        Header: 'Status',
        accessor: 'status',
      },
      {
        Header: 'Actions',
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
          const link = `#${this.props.location.pathname}/schedule/${props.value}`;
          return (
            <span>
              <a href={link}>
                <FontAwesomeIcon icon={['fas', 'pencil-alt']} size="1x" />
              </a>
            </span>
          );
        }
      }
    ];
    return (
      <Table
        data={data}
        columns={columns}
        loading={this.props.loading}
        pages={0}
        fetchDataCallback={() => { }} //TODO: CFLM-145: Deal with it
      />
    );
  }

  render = () => {
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
  loading: scheduleVisit.visitsLoading,
});

const mapDispatchToProps = ({
  getVisits
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ManageVisits);
