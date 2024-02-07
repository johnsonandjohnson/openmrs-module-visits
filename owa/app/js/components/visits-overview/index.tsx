/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { PropsWithIntl } from "../translation/PropsWithIntl";
import { IRootState } from "../../reducers";
import VisitsOverviewFiltersBar from "./filters-bar";
import IVisitsOverviewFilterData from "./data/IVisitsOverviewFilterData";
import IVisitsOverviewActionData from "./data/IVisitsOverviewActionData";
import VisitsOverviewActionsBar from "./actions-bar";
import "./index.scss";
import { getAppById } from '../../reducers/app';
import { RouteComponentProps } from 'react-router-dom';
import "../../../css/visits.scss";
import VisitsOverviewTable from "./table";
import { getVisitOverviewPage } from "../../reducers/overview-visits.reducer";
import _ from "lodash";
import { DEFAULT_ACTIVE_PAGE, DEFAULT_ITEMS_PER_PAGE } from '../overview-visits/table/constants';
import ScheduleVisitModal from "../schedule-visit/schedule-visit-modal";

interface IVisitsOverviewProps extends DispatchProps, StateProps, RouteComponentProps<{ patientUuid?: string }> {
  filters: IVisitsOverviewFilterData[]
  actions: IVisitsOverviewActionData
  appName: string
  patientUuid: string
  locationUuid: string
}

interface IVisitsOverviewState {
  filters: any
  selectedVisitsUuids: string[]
  isChangeVisitStatusButtonDisabled: boolean
  activePage: number
  itemsPerPage: number
  showScheduleVisitModal: boolean
  visitUuid: string | undefined
  isAllVisitsChecked: boolean
  sorted: { field: string, order: 'DESC' | 'ASC' }[]
}

class VisitsOverview extends React.Component<PropsWithIntl<IVisitsOverviewProps>, IVisitsOverviewState> {
  state = {
    filters: {
      locationUuid: this.props.locationUuid,
      patientUuid: this.props.patientUuid
    },
    selectedVisitsUuids: [],
    isChangeVisitStatusButtonDisabled: true,
    activePage: DEFAULT_ACTIVE_PAGE,
    itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
    showScheduleVisitModal: false,
    visitUuid: undefined,
    isAllVisitsChecked: false,
    sorted: []
  };

  componentDidMount() {
    this.props.getAppById(this.props.appName);
  }

  componentDidUpdate(prevProps: Readonly<PropsWithIntl<IVisitsOverviewProps>>, prevState: Readonly<IVisitsOverviewState>, snapshot?: any): void {
    if (!_.isEqual(prevState.filters, this.state.filters) || !_.isEqual(prevState.sorted, this.state.sorted) || prevState.activePage != this.state.activePage
      || prevState.itemsPerPage != this.state.itemsPerPage) {
      this.props.getVisitOverviewPage(this.state.activePage, this.state.itemsPerPage, this.state.filters, this.state.sorted);
    }
  }

  reloadData = (data: any) => {
    if (!!data) {
      this.setState({
        activePage: data.page,
        itemsPerPage: data.pageSize,
        sorted: data.sorted.map(sortedItem => ({ field: sortedItem.id, order: sortedItem.desc ? 'DESC' : 'ASC' }))
      });
    } else {
      this.props.getVisitOverviewPage(this.state.activePage, this.state.itemsPerPage, this.state.filters, this.state.sorted);
      this.setState({ selectedVisitsUuids: [], isChangeVisitStatusButtonDisabled: true, isAllVisitsChecked: false });
    }
  };

  handleOnFilterChange = (changedFilterValues: any) => {
    this.setState((prevState, props) => ({ filters: { ...prevState.filters, ...changedFilterValues } }));
    this.resetSelectedVisits();
  }

  resetSelectedVisits = () => {
    this.setState({
      isAllVisitsChecked: false,
      selectedVisitsUuids: [],
      isChangeVisitStatusButtonDisabled: true
    })
  }

  onCheckboxesChangeData = (data: any) => {
    this.setState({
      selectedVisitsUuids: data.selectedVisitsUuids,
      isChangeVisitStatusButtonDisabled: data.isChangeVisitStatusButtonDisabled,
      isAllVisitsChecked: data.isAllVisitsChecked
    })
  }

  onSelectAllCheckboxChange = (data: any) => {
    this.setState({
      isAllVisitsChecked: data.isAllVisitsChecked,
      selectedVisitsUuids: data.selectedVisitsUuids
    })
  }

  openScheduleVisitModal = (visitUuid?: string) => this.setState({
    showScheduleVisitModal: true,
    visitUuid: visitUuid
  });

  closeScheduleVisitModal = () => this.setState({ showScheduleVisitModal: false });

  saveVisitCallback = () => {
    this.setState(
      { showScheduleVisitModal: false }
    );
  };

  private helperText = (loading: boolean, totalCount: number) => {
    if (totalCount > 0) {
      return (
        <span>
          {this.props.intl.formatMessage({ id: "visits.overviewSelectedVisitsLabelPart1" })}
          {' '}
          {this.state.selectedVisitsUuids.length}
          {' '}
          {this.props.intl.formatMessage({ id: "visits.overviewSelectedVisitsLabelPart2" })}
          {' '}
          {totalCount}
          {' '}
          {this.props.intl.formatMessage({ id: "visits.overviewSelectedVisitsLabelPart3" })}
        </span>
      );
    } else if (!loading && totalCount === 0) {
      return this.props.intl.formatMessage({ id: "visits.overviewTableNoRecords" });
    }
  };

  render = () => {
    const app = this.props.app.app;
    return (
      <>
        {app &&
        <>
          <div className="search-section">
            <VisitsOverviewFiltersBar
              filters={app.config.filters}
              onFilterChange={this.handleOnFilterChange}
              resetSelectedVisits={this.resetSelectedVisits}
            />
            <VisitsOverviewActionsBar
              patientUuid={this.props.patientUuid}
              actions={app.config.actions}
              selectedVisitsUuids={this.state.selectedVisitsUuids}
              isChangeVisitStatusButtonDisabled={this.state.isChangeVisitStatusButtonDisabled}
              reloadDataCallback={this.reloadData}
              openScheduleVisitModalCallback={this.openScheduleVisitModal}
            />
            <div className="visit-table">
              <div className="helper-text">
                {this.props.loading ? (
                  <div className="spinner-border spinner-border-sm"/>
                ) : (
                  this.helperText(this.props.loading, this.props.totalCount)
                )}
              </div>
              <VisitsOverviewTable
                loading={false}
                columns={app.config.columns}
                data={this.props.visits.map((visit) => {
                  return {
                    ...visit
                  };
                })}
                pagesCount={this.props.pages}
                actions={app.config.actions}
                reloadDataCallback={this.reloadData}
                onCheckboxesChangeData={this.onCheckboxesChangeData}
                openScheduleVisitModalCallback={this.openScheduleVisitModal}
                onSelectAllCheckboxChange={this.onSelectAllCheckboxChange}
                selectedVisitsUuids={this.state.selectedVisitsUuids}
                isAllVisitsChecked={this.state.isAllVisitsChecked}
                defaultOrder={app.config.columns.defaultOrder}
              />
            </div>

            <ScheduleVisitModal
              show={this.state.showScheduleVisitModal}
              patientUuid={this.props.patientUuid}
              visitUuid={this.state.visitUuid}
              confirm={() => {
              }}
              cancel={this.closeScheduleVisitModal}
              saveVisitCallback={this.saveVisitCallback}
              refetchVisits={this.reloadData}
              locale={null}
            />
          </div>
        </>
        }
      </>
    );
  };
}

const mapStateToProps = ({ overview, app }: IRootState) => ({
  app,
  visits: overview.visits,
  pages: overview.pages,
  totalCount: overview.totalCount,
  loading: overview.loading,
});

const mapDispatchToProps = ({
  getAppById,
  getVisitOverviewPage
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(VisitsOverview));
