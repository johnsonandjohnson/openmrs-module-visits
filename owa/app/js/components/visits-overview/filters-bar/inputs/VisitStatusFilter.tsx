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
import { PropsWithIntl } from "../../../translation/PropsWithIntl";
import { IRootState } from "../../../../reducers";
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import Select from "react-select";
import _ from "lodash";

interface ISelectProps extends DispatchProps, StateProps {
  label: string
  options: any[]
  onFilterChange: (filterValue: any) => void
  registerClearStateCallback: (clearStateCallback: () => void) => void
}

interface IFilters {
  visitStatus: {
    label: string;
    value: string;
  } | null;
}

interface ISelectState {
  filters: IFilters;
}

const CLEAR_STATE = {
  filters: {
    visitStatus: null,
  }
};

class VisitStatusFilter extends React.Component<PropsWithIntl<ISelectProps>, ISelectState> {
  state = CLEAR_STATE;

  componentDidMount(): void {
    this.props.registerClearStateCallback(this.clearState);
    this.doFilterChange();
  }

  componentDidUpdate(prevProps: Readonly<PropsWithIntl<ISelectProps>>, prevState: Readonly<ISelectState>, snapshot?: any): void {
    if (!_.isEqual(prevState.filters.visitStatus, this.state.filters.visitStatus)) {
      this.doFilterChange();
    }
  }

  doFilterChange = () => {
    const visitStatus = _.get(this.state.filters, "visitStatus.value", null);
    this.props.onFilterChange({ visitStatus });
  };

  clearState = () => {
    this.setState(CLEAR_STATE);
  };

  render = () => {
    return (
      <Select
        options={this.props.options}
        onChange={(visitStatus) => this.setState((state) => ({ filters: { ...state.filters, visitStatus } }))}
        className="visits-select"
        classNamePrefix="visits-select"
        placeholder={this.props.intl.formatMessage({
          id: "visits.overviewVisitStatusPlaceholder"
        })}
        value={this.state.filters.visitStatus}
        isSearchable={false}
        isClearable
        theme={theme => ({
          ...theme,
          colors: {
            ...theme.colors,
            primary: "#00455c",
            primary25: "#e4e7e7"
          }
        })}
      />

    )
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(VisitStatusFilter));
