import React from "react";
import {PropsWithIntl} from "../../../translation/PropsWithIntl";
import {Button} from "react-bootstrap";
import {IRootState} from "../../../../reducers";
import {connect} from "react-redux";
import {injectIntl} from 'react-intl';

interface IProps extends DispatchProps, StateProps {
  patientUuid: string
  openScheduleVisitModalCallback: (visitUuid: string | null) => void
}

interface IState {
}

class ScheduleVisitAction extends React.Component<PropsWithIntl<IProps>, IState> {
  render = () => {
    return (
      <>
        <Button
          id="visit-schedule-button"
          className="btn btn-success btn-md add-btn"
          onClick={() => this.props.openScheduleVisitModalCallback(null)}
        >
          {this.props.intl.formatMessage({id: "visits.scheduleVisit"})}
        </Button>
      </>
    );
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduleVisitAction));

