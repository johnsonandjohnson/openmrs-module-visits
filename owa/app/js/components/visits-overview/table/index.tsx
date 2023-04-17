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
import ReactTable from "react-table";
import { connect } from 'react-redux';
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import { DEFAULT_ACTIVE_PAGE, DEFAULT_ITEMS_PER_PAGE, MIN_ROWS, PAGE_SIZE_OPTIONS } from "../../overview-visits/table/constants";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStethoscope } from "@fortawesome/free-solid-svg-icons";
import IVisit from '../../../shared/model/visit';
import DeleteRowAction from "./delete-row-action";
import OverviewTableCell from "./OverviewTableCell";
import EditVisitAction from "./edit-visit-action";
import IModalParams from '../../manage-visits/modal-params';

const OPEN_MRS_ROUTE = '../..';

export interface ITableColumn {
  label: string
  field: string
  dateTimeFormat?: string
  sortable?: boolean
}

interface ITableData {
  uuid: string
}

interface IConfigActions {
  changeVisitsStatus: boolean
  scheduleNewVisit: boolean
}

interface IVisitsOverviewTableProps extends DispatchProps, StateProps {
  loading: boolean
  columns: ITableColumn[]
  data: ITableData[]
  pagesCount: number
  actions: IConfigActions
  isAllVisitsChecked: boolean
  selectedVisitsUuids: string[];
  reloadDataCallback: (data: any) => void;
  onCheckboxesChangeData: (data: any) => void
  openScheduleVisitModalCallback: (visitUuid: string | null) => void
  onSelectAllCheckboxChange: (data: any) => void
}

interface IVisitsOverviewTableState {
  activePage: number;
  itemsPerPage: number;
  showEditVisitModal: boolean;
  modalParams: IModalParams | null
}

class VisitsOverviewTable extends React.Component<PropsWithIntl<IVisitsOverviewTableProps>, IVisitsOverviewTableState> {
  fetchData = (state, instance) => {
    this.setState({
        activePage: state.page,
        itemsPerPage: state.pageSize
      },
      () => this.props.reloadDataCallback(state))
  };

  state = {
    activePage: DEFAULT_ACTIVE_PAGE,
    itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
    showEditVisitModal: false,
    modalParams: null
  }

  private getReturnUrlParamForCurrentLocation() {
    const pathCorrectionString = '?'; // used to compensate OpenMRS returnUrl invalid handling
    let url = location.href.replace(location.origin, '');
    url = encodeURIComponent(`${url}${pathCorrectionString}`);
    const paramName = 'returnUrl';
    return `${paramName}=${url}`;
  }

  private handleOnClickVisitCheckbox = e => {
    e.stopPropagation();
    const checkBoxElement = e.target;
    const visitUuid = checkBoxElement.id;
    let selectedVisitsUuids = this.props.selectedVisitsUuids;
    let searchUuid = selectedVisitsUuids.indexOf(visitUuid);
    if (searchUuid > -1) {
      selectedVisitsUuids.splice(searchUuid, 1);
    } else {
      selectedVisitsUuids.push(visitUuid);
    }

    
    const isAnyVisitSelected = selectedVisitsUuids.length > 0;
    let isChangeVisitStatusButtonDisabled = true;
    let isAllVisitsChecked = this.props.isAllVisitsChecked;
    if (isAnyVisitSelected) {
      isChangeVisitStatusButtonDisabled = false
    } else {
      isAllVisitsChecked = false;
    }

    this.props.onCheckboxesChangeData({
      selectedVisitsUuids,
      isChangeVisitStatusButtonDisabled,
      isAllVisitsChecked
    });
  }

  private handleSelectAll = e => {
    const selectAllCheckbox = e.target as HTMLInputElement;
    const isSelectAllCheckboxChecked = selectAllCheckbox.checked;

    let isAllVisitsChecked = false;
    let selectedVisitsUuids: string[] = [];
    let isChangeVisitStatusButtonDisabled = true;

    if (isSelectAllCheckboxChecked) {
      isAllVisitsChecked = true;
      selectedVisitsUuids = this.props.data.map(({ uuid }) => uuid);
      if (selectedVisitsUuids.length > 0) {
        isChangeVisitStatusButtonDisabled = false;
      }
    }

    this.props.onCheckboxesChangeData({
      selectedVisitsUuids,
      isChangeVisitStatusButtonDisabled
    });

    this.props.onSelectAllCheckboxChange({
      isAllVisitsChecked,
      selectedVisitsUuids
    });
  }

  getColumns = () => {
    return [
      this.props.actions.changeVisitsStatus ?
        {
          width: 50,
          accessor: "uuid",
          Header: () => {
            return (
              <div>
                <input
                  id="selectAllCheckbox"
                  type="checkbox"
                  className="omrs-checkbox"
                  checked={this.props.isAllVisitsChecked}
                  onClick={this.handleSelectAll}
                />
              </div>
            );
          },
          Cell: ({ value }) => {
            return (
              <div>
                <input
                  id={value}
                  type="checkbox"
                  className="omrs-checkbox"
                  checked={this.props.selectedVisitsUuids.includes(value)}
                  onClick={this.handleOnClickVisitCheckbox}/>
              </div>
            );
          }
        } : {
          width: 0,
          accessor: "",
          Header: () => {},
          Cell: () => {}
        },
      ...this.props.columns.map(column => {
        return {
          Header: this.props.intl.formatMessage({ id: `${column.label}` }),
          accessor: column.field,
          sortable: column.sortable,
          defaultSortDesc: column.sortable ? true : undefined,
          Cell: (props) => {
            return <OverviewTableCell column={column} value={props.value}/>
          },
        };
      }),
      {
        Header: this.props.intl.formatMessage({ id: "visits.actionsColumnLabel" }),
        getProps: () => {
          return {
            className: 'action-column'
          };
        },
        Cell: (row) => {
          const { viewIndex } = row;
          const visit: IVisit = row.original;
          return (
            <>
              <EditVisitAction
                viewIndex={viewIndex}
                visit={visit}
                activePage={this.state.activePage}
                itemsPerPage={this.state.itemsPerPage}
                openScheduleVisitModalCallback={this.props.openScheduleVisitModalCallback}
              />
              {!!visit.formUri ? (
                <span className="action-button">
                  <a
                    id={`visit-note-button-${viewIndex}`}
                    href={`${OPEN_MRS_ROUTE}${visit.formUri}&${this.getReturnUrlParamForCurrentLocation()}`}
                  >
                    <FontAwesomeIcon icon={faStethoscope} size="1x"/>
                  </a>
                </span>
              ) : (
                <span className="action-button"></span>
              )
              }
              {visit.actualDate == null ?
                <DeleteRowAction
                  viewIndex={viewIndex}
                  visitUuid={visit.uuid}
                  refreshTableCallback={this.props.reloadDataCallback}
                /> :
                <span className="action-button"></span>
              }
            </>
          );
        }
      }
    ];
  };

  render = () => {
    const NullComponent = () => null;
    const noDataText = this.props.intl.formatMessage({ id: 'visits.overviewNoResultsFoundLabel' });
    const previousText = this.props.intl.formatMessage({ id: 'visits.overviewPreviousLabel' });
    const nextText = this.props.intl.formatMessage({ id: 'visits.overviewNextLabel' });
    const loadingText = this.props.intl.formatMessage({ id: 'visits.overviewLoadingLabel' });
    const pageText = this.props.intl.formatMessage({ id: 'visits.overviewPageLabel' });
    const ofText = this.props.intl.formatMessage({ id: 'visits.overviewOfLabel' });
    const rowsText = this.props.intl.formatMessage({ id: 'visits.overviewResultsLabel' });

    return (
      <ReactTable
        className="-striped -highlight"
        collapseOnDataChange={false}
        manual={true}
        multisort={false}
        columns={this.getColumns()}
        data={this.props.data}
        pages={this.props.pagesCount}
        loading={this.props.loading}
        onFetchData={this.fetchData}
        defaultPageSize={DEFAULT_ITEMS_PER_PAGE}
        defaultSorted={this.props.columns.filter(column => column.sortable).map(column => ({
          id: column.field,
          desc: true
        }))}
        minRows={MIN_ROWS}
        pageSizeOptions={PAGE_SIZE_OPTIONS}
        nextText={nextText}
        previousText={previousText}
        rowsText={rowsText}
        loadingText={loadingText}
        ofText={ofText}
        pageText={pageText}
        noDataText={noDataText}
        NoDataComponent={NullComponent}
        showPagination={true}
        sortable={false}
        multiSort={false}
        resizable={false}
      />
    );
  };
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(VisitsOverviewTable));
