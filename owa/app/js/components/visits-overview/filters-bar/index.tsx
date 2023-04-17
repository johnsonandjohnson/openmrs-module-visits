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
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import VisitsOverviewFilter from "./filter";
import IVisitsOverviewFilterData from "../data/IVisitsOverviewFilterData";
import { Form, FormGroup } from "reactstrap";

interface IVisitsOverviewFiltersBarProps extends DispatchProps, StateProps {
  filters: IVisitsOverviewFilterData[]
  onFilterChange: (filterValue: any) => void
  resetSelectedVisits: () => void
}

interface IVisitsOverviewFiltersBarState {

}

class VisitsOverviewFiltersBar extends React.Component<PropsWithIntl<IVisitsOverviewFiltersBarProps>, IVisitsOverviewFiltersBarState> {
  clearStateCallbacks: { (): void } [] = [];

  handleResetButton = () => {
    this.clearStateCallbacks.forEach(clearStateCallback => clearStateCallback())
  };

  registerClearStateCallback = (clearStateCallback: () => void) => {
    this.clearStateCallbacks.push(clearStateCallback);
  };

  renderFilters = () => {
    return this.props.filters.map(filter => {
      return <VisitsOverviewFilter 
        onFilterChange={this.props.onFilterChange} 
        registerClearStateCallback={this.registerClearStateCallback} 
        filterData={filter}
      />;
    });
  };

  renderClearFiltersButton = () => {
    return (
      <div className="reset-filter" onClick={this.handleResetButton}>
        <span className="reset-filter-text">
          {this.props.intl.formatMessage({id: "visits.overviewClearFiltersLabel"})}
        </span>
      </div>
    );
  };

  render = () => {
    return (
      <Form>
        <FormGroup className="visit-search">
          {this.renderFilters()}
          {this.renderClearFiltersButton()}
        </FormGroup>
      </Form>
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
)(VisitsOverviewFiltersBar));

