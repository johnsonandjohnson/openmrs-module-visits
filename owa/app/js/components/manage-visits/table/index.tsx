/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import ReactTable from 'react-table';
import { LocalizedMessage } from '@openmrs/react-components';
import _ from 'lodash';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import {
  DEFAULT_ITEMS_PER_PAGE,
  DEFAULT_SORT,
  DEFAULT_ORDER,
  MIN_ROWS,
  PAGE_SIZE_OPTIONS
} from './constants';
import {
  ACTIONS_COLUMN_LABEL,
  MANAGE_VISITS_COLUMNS
} from '../../../shared/utils/messages';
import { withFiltersChangedCallback } from './with-filters-changed-callback';
import IVisit from '../../../shared/model/visit';
import ITableParams from '../table-params';
import IModalParams, { createModalParams } from '../modal-params';

const OPEN_MRS_ROUTE = '../..';

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
  fetchDataCallback(params: ITableParams): void;
  removeCallback(params: IModalParams): void;
};

export default class ManageVisitTable extends React.PureComponent<ITableProps, IPaginationBaseState> {
  filters: any;
  constructor(props) {
    super(props);
    this.state = {
      activePage: 0,
      itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
      sort: DEFAULT_SORT,
      order: DEFAULT_ORDER,
      filters: {}
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
      query
    };
  }

  componentDidUpdate(prevProps) {
    if (prevProps.query != this.props.query) {
      this.props.fetchDataCallback(this.stateToTableParams());
    }
  }

  private getActionsColumn = () => {
    return {
      Header: ACTIONS_COLUMN_LABEL,
      getProps: () => {
        return {
          className: 'action-column'
        };
      },
      Cell: (row) => {
        const visit: IVisit = row.original;
        const editLink = `#${this.props.createPathname}/schedule/${visit.uuid}`;
        return (
          <>
            <span className="action-button">
              <a href={editLink}>
                <FontAwesomeIcon icon={['fas', 'pencil-alt']} size={'1x'} />
              </a>
            </span>
            { (!!visit.formUri) ?
              <span className="action-button">
              <a href={`${OPEN_MRS_ROUTE}${visit.formUri}`}>
                <i className="small icon-stethoscope"/>
              </a>
            </span> : null
            }
            <span className="action-button">
              <i className="small icon-remove delete-action interaction-trash-button"
                onClick={() => {
                  this.remove(visit);
                }} />
            </span>
          </>
        );
      }
    };
  }

  remove = (visit: IVisit) => {
    const modalParams: IModalParams = createModalParams(visit, this.stateToTableParams());
    this.props.removeCallback(modalParams);
  }

  getColumns = () => {
    return [
      ...MANAGE_VISITS_COLUMNS,
      this.getActionsColumn()
    ];
  }

  fetchData = (state, instance) => {
    this.setState(
      {
        activePage: state.page,
        itemsPerPage: state.pageSize,
        sort: state.sorted[0] ? state.sorted[0].id : DEFAULT_SORT,
        order: state.sorted[0] ? (state.sorted[0].desc ? 'desc' : 'asc') : DEFAULT_ORDER
      },
      () => this.props.fetchDataCallback(this.stateToTableParams())
    );
  }

  filtersChanged = (changedFilters: {}) => this.setState(((prevState, props) => ({ filters: { ...prevState.filters, ...changedFilters } })),
    () => this.props.fetchDataCallback(this.stateToTableParams()));

  render = () => {
    const noDataText = <LocalizedMessage id="reactcomponents.table.noDataText" defaultMessage="No results found" />;
    const previousText = <LocalizedMessage id="reactcomponents.table.previous" defaultMessage="Previous" />;
    const nextText = <LocalizedMessage id="reactcomponents.table.next" defaultMessage="Next" />;
    const loadingText = <LocalizedMessage id="reactcomponents.table.loading" defaultMessage="Loading..." />;
    const pageText = <LocalizedMessage id="reactcomponents.table.page" defaultMessage="Page" />;
    const ofText = <LocalizedMessage id="reactcomponents.table.of" defaultMessage="of" />;
    const rowsText = <LocalizedMessage id="reactcomponents.table.rows" defaultMessage="rows" />;

    return (
      <div>
        {this.props.filtersComponent && <this.filters {...this.props.filterProps} {...this.state.filters} {...this.props.filterProps} />}
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
          multisort={false}
          nextText={nextText}
          previousText={previousText}
          rowsText={rowsText}
          loadingText={loadingText}
          ofText={ofText}
          noDataText={<span className="sortableTable-noDataText">{noDataText}</span>}
          pageText={pageText}
          showPagination={_.get(this.props, 'showPagination', true)}
          sortable={_.get(this.props, 'sortable', true)}
          multiSort={_.get(this.props, 'multiSort', true)}
          resizable={_.get(this.props, 'resizable', true)}
        />
      </div>
    );
  }
}
