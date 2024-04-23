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
import ReactTable from "react-table";
import _ from "lodash";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { DEFAULT_ITEMS_PER_PAGE, DEFAULT_SORT, DEFAULT_ORDER, MIN_ROWS, PAGE_SIZE_OPTIONS } from "./constants";
import { withFiltersChangedCallback } from "./with-filters-changed-callback";
import IVisit from "../../../shared/model/visit";
import ITableParams from "../table-params";
import IModalParams, { createModalParams } from "../modal-params";
import { faPencilAlt, faStethoscope } from "@fortawesome/free-solid-svg-icons";
import { faTrashAlt } from "@fortawesome/free-regular-svg-icons";
import { FormattedMessage } from "react-intl";
import { PropsWithIntl } from "../../../components/translation/PropsWithIntl";

const OPEN_MRS_ROUTE = "../..";

interface IPaginationBaseState {
  itemsPerPage: number;
  sort: string;
  order: string;
  activePage: number;
  filters: {};
}

export interface ITableProps {
  data: any[];
  loading: boolean;
  pages: number;
  createPathname: string;
  query?: string;
  filtersComponent?: any;
  filterProps?: {};
  showPagination?: boolean;
  sortable?: boolean;
  multiSort?: boolean;
  resizable?: boolean;
  noDataText?: string;
  locale?: string;
  fetchDataCallback(params: ITableParams): void;
  deleteCallback(params: IModalParams): void;
  updateCallback(params: IModalParams): void;
}

export default class ManageVisitTable extends React.PureComponent<PropsWithIntl<ITableProps>, IPaginationBaseState> {
  filters: any;
  constructor(props) {
    super(props);
    this.state = {
      activePage: 0,
      itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
      sort: DEFAULT_SORT,
      order: DEFAULT_ORDER,
      filters: {},
    };
    if (this.props.filtersComponent) {
      this.filters = withFiltersChangedCallback(this.filtersChanged)(this.props.filtersComponent);
    }
  }

  stateToTableParams = (): ITableParams => {
    const query = this.props.query;
    const { activePage, itemsPerPage, sort, order, filters } = this.state;
    return {
      activePage,
      itemsPerPage,
      sort,
      order,
      filters,
      query,
    };
  };

  componentDidUpdate(prevProps) {
    if (prevProps.query != this.props.query) {
      this.props.fetchDataCallback(this.stateToTableParams());
    }
  }

  private getReturnUrlParamForCurrentLocation() {
    const pathCorrectionString = "?"; // used to compensate OpenMRS returnUrl invalid handling
    let url = location.href.replace(location.origin, "");
    url = encodeURIComponent(`${url}${pathCorrectionString}`);
    const paramName = "returnUrl";
    return `${paramName}=${url}`;
  }

  private getActionsColumn = () => {
    return {
      Header: this.props.intl.formatMessage({ id: "visits.actionsColumnLabel" }),
      getProps: () => {
        return {
          className: "action-column",
        };
      },
      Cell: (row) => {
        const { viewIndex } = row;
        const visit: IVisit = row.original;

        return (
          <>
            <span className="action-button">
              <i id={`visit-update-button-${viewIndex}`} onClick={() => this.update(visit)}>
                <FontAwesomeIcon icon={faPencilAlt} size="1x" />
              </i>
            </span>
            {!!visit.formUri ? (
              <span className="action-button">
                <a
                  id={`visit-note-button-${viewIndex}`}
                  href={`${OPEN_MRS_ROUTE}${visit.formUri}&${this.getReturnUrlParamForCurrentLocation()}`}
                >
                  <FontAwesomeIcon icon={faStethoscope} size="1x" />
                </a>
              </span>
            ) : (
              <span className="action-button"></span>
            )}
            <span className="action-button">
              <i id={`visit-delete-button-${viewIndex}`} onClick={() => this.delete(visit)}>
                <FontAwesomeIcon icon={faTrashAlt} size="1x" />
              </i>
            </span>
          </>
        );
      },
    };
  };

  delete = (visit: IVisit) => {
    const modalParams: IModalParams = createModalParams(visit, this.stateToTableParams());
    this.props.deleteCallback(modalParams);
  };

  update = (visit: IVisit) => {
    const modalParams: IModalParams = createModalParams(visit, this.stateToTableParams());
    this.props.updateCallback(modalParams);
  };

  getColumns = () => {
    return [
      { Header: this.props.intl.formatMessage({ id: "visits.visitPlannedDateLabel" }), accessor: "startDate" },
      { Header: this.props.intl.formatMessage({ id: "visits.visitActualDateLabel" }), accessor: "actualDate" },
      { Header: this.props.intl.formatMessage({ id: "visits.timeColumnLabel" }), accessor: "time" },
      { Header: this.props.intl.formatMessage({ id: "visits.locationColumnLabel" }), accessor: "locationName" },
      { Header: this.props.intl.formatMessage({ id: "visits.typeColumnLabel" }), accessor: "typeName" },
      { Header: this.props.intl.formatMessage({ id: "visits.statusColumnLabel" }), accessor: "status" },
      this.getActionsColumn(),
    ];
  };

  fetchData = (state, instance) => {
    this.setState(
      {
        activePage: state.page,
        itemsPerPage: state.pageSize,
        sort: state.sorted[0] ? state.sorted[0].id : DEFAULT_SORT,
        order: state.sorted[0] ? (state.sorted[0].desc ? "desc" : "asc") : DEFAULT_ORDER,
      },
      () => this.props.fetchDataCallback(this.stateToTableParams()),
    );
  };

  filtersChanged = (changedFilters: {}) =>
    this.setState(
      (prevState, props) => ({ filters: { ...prevState.filters, ...changedFilters } }),
      () => this.props.fetchDataCallback(this.stateToTableParams()),
    );

  render = () => {
    const NullComponent = () => null;
    const noDataText = <FormattedMessage id="visits.overviewNoResultsFoundLabel" />;
    const previousText = <FormattedMessage id="visits.overviewPreviousLabel" />;
    const nextText = <FormattedMessage id="visits.overviewNextLabel" />;
    const loadingText = <FormattedMessage id="visits.overviewLoadingLabel" />;
    const pageText = <FormattedMessage id="visits.overviewPageLabel" />;
    const ofText = <FormattedMessage id="visits.overviewOfLabel" />;
    const rowsText = this.props.intl.formatMessage({ id: "visits.overviewResultsLabel" });

    return (
      <div>
        {this.props.filtersComponent && (
          <this.filters {...this.props.filterProps} {...this.state.filters} {...this.props.filterProps} />
        )}
        <ReactTable
          className="-striped -highlight"
          collapseOnDataChange={false}
          columns={this.getColumns()}
          data={this.props.data}
          defaultPageSize={DEFAULT_ITEMS_PER_PAGE}
          manual={true}
          loading={this.props.loading}
          minRows={MIN_ROWS}
          pages={this.props.pages}
          pageSizeOptions={PAGE_SIZE_OPTIONS}
          onFetchData={this.fetchData}
          nextText={nextText}
          previousText={previousText}
          rowsText={rowsText}
          loadingText={loadingText}
          ofText={ofText}
          noDataText={_.get(this.props, "noDataText", <span className="sortableTable-noDataText">{noDataText}</span>)}
          NoDataComponent={NullComponent}
          pageText={pageText}
          showPagination={_.get(this.props, "showPagination", true)}
          sortable={_.get(this.props, "sortable", true)}
          multiSort={_.get(this.props, "multiSort", true)}
          resizable={_.get(this.props, "resizable", true)}
        />
      </div>
    );
  };
}
