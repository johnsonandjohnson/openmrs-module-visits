import React from "react";
import { injectIntl } from 'react-intl';
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import { connect } from "react-redux";
import { ITableColumn } from "./index";
import moment from "moment";

interface IOverviewTableCellProps extends DispatchProps, StateProps {
  column: ITableColumn
  value: any
}

interface IOverviewTableCellState {

}

class OverviewTableCell extends React.Component<PropsWithIntl<IOverviewTableCellProps>, IOverviewTableCellState> {
  render = () => {
    if (!!this.props.column.dateTimeFormat) {
      const formattedDatetime = !!this.props.value ? moment(this.props.value).format(this.props.column.dateTimeFormat) : null;
      return (
        <div className="td-cell">{formattedDatetime}</div>
      );
    } else {
      return (
        <div className="td-cell">{this.props.value}</div>
      )
    }
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(OverviewTableCell));
