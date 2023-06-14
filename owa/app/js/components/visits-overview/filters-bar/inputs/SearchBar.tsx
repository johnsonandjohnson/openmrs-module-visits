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
import { IRootState } from "../../../../reducers";
import { connect } from 'react-redux';
import { PropsWithIntl } from "../../../translation/PropsWithIntl";
import { injectIntl } from 'react-intl';
import { Input } from "reactstrap";
import "../.././index.scss";

const searchIcon = require("../../../../../img/search.png");

interface ISearchBarProps extends DispatchProps, StateProps {
  label: string
  onFilterChange: (filterValue: any) => void
  registerClearStateCallback: ( clearStateCallback: () => void ) => void
}

interface ISearchBarState {
  query: string;
}

const DEFAULT_STATE = {
  query: ""
};

const CLEAR_STATE = DEFAULT_STATE;

class SearchBar extends React.Component<PropsWithIntl<ISearchBarProps>, ISearchBarState> {
  state = DEFAULT_STATE;

  componentDidMount(): void {
    this.props.registerClearStateCallback(this.clearState);
  }

  componentDidUpdate(prevProps: Readonly<PropsWithIntl<ISearchBarProps>>, prevState: Readonly<ISearchBarState>, snapshot?: any): void {
    if (prevState.query != this.state.query) {
      this.props.onFilterChange({ query: this.state.query });
    }
  }

  clearState = () => {
    this.setState(CLEAR_STATE);
  };

  private onQueryChange = (event: any) => this.setState({ query: event.target.value });

  render = () => {
    return (
      <>
        <div className="search-bar">
          <img src={searchIcon} alt="search" className="search-icon"/>
          <Input
            id="visits-overview-filter-name"
            placeholder={this.props.intl.formatMessage({ id: this.props.label })}
            className="search-input form-control"
            onChange={this.onQueryChange}
            value={this.state.query}
          />
        </div>
      </>
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
)(SearchBar));
