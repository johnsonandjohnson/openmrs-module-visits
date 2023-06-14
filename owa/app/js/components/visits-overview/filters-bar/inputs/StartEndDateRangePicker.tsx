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
import { DateRangePicker } from "react-dates";
import moment from "moment";

const OVERVIEW_DATE_FORMAT = "DD MMM YYYY";
const MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH = 768;

interface IDateRangePickerProps extends DispatchProps, StateProps {
  label: string
  onFilterChange: (filterValue: any) => void
  registerClearStateCallback: (clearStateCallback: () => void) => void
}

interface IFilters {
  dateFrom: moment.Moment | null;
  dateTo: moment.Moment | null;
}

interface IDateRangePickerState {
  filters: IFilters;
  focusedDatePicker: any;
}

const DEFAULT_STATE = {
  filters: {
    dateFrom: moment(),
    dateTo: moment(),
  },
  focusedDatePicker: null
};

const CLEAR_STATE = {
  filters: {
    dateFrom: null,
    dateTo: null,
  },
  focusedDatePicker: null
};

class StartEndDateRangePicker extends React.Component<PropsWithIntl<IDateRangePickerProps>, IDateRangePickerState> {
  state = DEFAULT_STATE;

  componentDidMount(): void {
    this.props.registerClearStateCallback(this.clearState);
    this.notifyDateChanged();
  }

  componentDidUpdate(prevProps: Readonly<PropsWithIntl<IDateRangePickerProps>>, prevState: Readonly<IDateRangePickerState>, snapshot?: any): void {
    if (prevState.filters.dateFrom != this.state.filters.dateFrom || prevState.filters.dateTo != this.state.filters.dateTo) {
      this.notifyDateChanged();
    }
  }

  clearState = () => {
    this.setState(CLEAR_STATE);
  };

  private notifyDateChanged = () => {
    const MILLISECONDS_IN_SEC = 1000;
    const dateFromMoment = this.state.filters.dateFrom;
    const dateToMoment = this.state.filters.dateTo;

    this.props.onFilterChange({
      dateFrom: !!dateFromMoment ? dateFromMoment.startOf("day").unix() * MILLISECONDS_IN_SEC : null,
      dateTo: !!dateToMoment ? dateToMoment.endOf("day").unix() * MILLISECONDS_IN_SEC : null
    });
  };

  render = () => {
    const {
      focusedDatePicker,
      filters: {
        dateFrom,
        dateTo,
      },
    } = this.state;

    return (
      <DateRangePicker
        startDate={dateFrom}
        startDatePlaceholderText={this.props.intl.formatMessage({ id: 'visits.overviewStartDate' })}
        endDate={dateTo}
        endDatePlaceholderText={this.props.intl.formatMessage({ id: 'visits.overviewEndDate' })}
        onDatesChange={({ startDate, endDate }) =>
          this.setState((state) => ({ filters: { ...state.filters, dateFrom: startDate, dateTo: endDate } }))
        }
        focusedInput={focusedDatePicker}
        onFocusChange={(focusedDatePicker) => this.setState({ focusedDatePicker })}
        showClearDates={true}
        displayFormat={OVERVIEW_DATE_FORMAT}
        hideKeyboardShortcutsPanel
        isOutsideRange={() => false}
        showDefaultInputIcon
        orientation={window.screen.availWidth > MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH ? "horizontal" : "vertical"}
        small={window.screen.availWidth < MIN_HORIZONTAL_DATE_RANGE_PICKER_WIDTH}
        disabled={false}
        minimumNights={0}
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
)(StartEndDateRangePicker));
