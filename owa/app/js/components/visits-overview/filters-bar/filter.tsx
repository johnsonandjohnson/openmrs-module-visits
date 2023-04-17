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
import IVisitsOverviewFilterData from "../data/IVisitsOverviewFilterData";
import SearchBar from "./inputs/SearchBar";
import StartEndDateRangePicker from "./inputs/StartEndDateRangePicker";
import VisitStatusFilter from "./inputs/VisitStatusFilter";

interface IVisitsOverviewFilterProps extends DispatchProps, StateProps {
  filterData: IVisitsOverviewFilterData
  onFilterChange: (filterValue: any) => void
  registerClearStateCallback: (clearStateCallback: () => void) => void
}

interface IVisitsOverviewFilterState {

}

const DEFAULT_FIELD_TYPE = 'text';
const DEFAULT_INPUT = SearchBar;
const INPUTS_BY_TYPE = {
  text: SearchBar,
  dateRange: StartEndDateRangePicker,
  select: VisitStatusFilter
};

class VisitsOverviewFilter extends React.Component<PropsWithIntl<IVisitsOverviewFilterProps>, IVisitsOverviewFilterState> {
  render = () => {
    const type = this.props.filterData.type|| DEFAULT_FIELD_TYPE;
    const InputComponent = INPUTS_BY_TYPE[type] || DEFAULT_INPUT;
    return (
      <InputComponent 
        onFilterChange={this.props.onFilterChange} 
        registerClearStateCallback={this.props.registerClearStateCallback} {...this.props.filterData} 
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
)(VisitsOverviewFilter));
