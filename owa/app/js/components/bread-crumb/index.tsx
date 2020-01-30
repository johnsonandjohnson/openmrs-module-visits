
import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';
import './bread-crumb.scss';
import { UnregisterCallback } from 'history';
import * as Msg from '../../shared/utils/messages';
import { getPatient } from '../../reducers/patient.reducer';
import { IRootState } from '../../reducers';

const EDIT_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid/schedule/:visitUuid`);
const OVERVIEW_VISIT_PATTERN = new UrlPattern(`/visits/overview`);
const SCHEDULE_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid/schedule`);
const MANAGE_VISIT_PATTERN = new UrlPattern(`/visits/manage/:patientUuid*`);

const MODULE_ROUTE = '/';
const OMRS_ROUTE = '../../';
const OVERVIEW_VISITS_ROUTE = `/visits/overview`;
const MANAGE_VISITS_ROUTE = patientUuid => `/visits/manage/${patientUuid}`;
const PATIENT_DASHBOARD_ROUTE = patientUuid => `${OMRS_ROUTE}coreapps/clinicianfacing/patient.page?patientId=${patientUuid}`;

interface IBreadCrumbProps extends DispatchProps, StateProps, RouteComponentProps {
};

interface IBreadCrumbState {
  current: string
};

class BreadCrumb extends React.PureComponent<IBreadCrumbProps, IBreadCrumbState>{
  unlisten: UnregisterCallback;

  constructor(props: IBreadCrumbProps) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname;
      this.setState({ current });
    });
  }

  componentWillUnmount = () => this.unlisten();


  buildBreadCrumb = () =>
    <div className="breadcrumb">
      {this.renderCrumbs(this.getCrumbs(this.state.current))}
    </div>


  getCrumbs = (path: string): Array<ReactFragment> => {
    if (!!OVERVIEW_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getOverviewVisitCrumbs();
    } if (!!SCHEDULE_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getScheduleVisitCrumbs(path);
    } if (!!EDIT_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getEditVisitCrumbs(path);
    } else if (!!MANAGE_VISIT_PATTERN.match(path.toLowerCase())) {
      return this.getManageVisitCrumbs(path);
    } else {
      return [this.renderLastCrumb(Msg.GENERAL_MODULE_BREADCRUMB)];
    }
  }

  getPatientNameCrumb = (path: string, pattern: UrlPattern) => {
    const match = pattern.match(path.toLowerCase());
    const patientUuid = match.patientUuid;

    if (this.props.patient.uuid != patientUuid) {
      this.props.getPatient(patientUuid);
    }

    const patientName = this.props.patient.person ? this.props.patient.person.display : '';
    return this.renderCrumb(PATIENT_DASHBOARD_ROUTE(patientUuid), patientName, true)
  }

  getManageVisitsCrumb = (path: string, pattern: UrlPattern) => {
    const match = pattern.match(path.toLowerCase());
    return this.renderCrumb(
      MANAGE_VISITS_ROUTE(match.patientUuid),
      Msg.MANAGE_VISITS_BREADCRUMB
    );
  }

  getOverviewVisitCrumbs = (): Array<ReactFragment> => [
    this.renderLastCrumb(Msg.OVERVIEW_VISIT_BREADCRUMB)
  ];

  getScheduleVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientNameCrumb(path, SCHEDULE_VISIT_PATTERN),
    this.getManageVisitsCrumb(path, SCHEDULE_VISIT_PATTERN),
    this.renderLastCrumb(Msg.SCHEDULE_VISIT_BREADCRUMB)
  ];

  getEditVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientNameCrumb(path, EDIT_VISIT_PATTERN),
    this.getManageVisitsCrumb(path, EDIT_VISIT_PATTERN),
    this.renderLastCrumb(Msg.EDIT_VISIT_BREADCRUMB)
  ];

  getManageVisitCrumbs = (path: string): Array<ReactFragment> => [
    this.getPatientNameCrumb(path, MANAGE_VISIT_PATTERN),
    this.renderLastCrumb(Msg.MANAGE_VISITS_BREADCRUMB)
  ];

  renderCrumbs = (elements: Array<any>) => {
    const delimiter = this.renderDelimiter();
    const lastElementId = elements.length - 1;
    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        { (0 < elements.length) && delimiter}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {e}
            {i !== lastElementId && delimiter}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  renderDelimiter = () =>
    <span className="breadcrumb-link-item">
      <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
    </span>

  renderHomeCrumb = () =>
    <a href={OMRS_ROUTE} className="breadcrumb-link-item">
      <FontAwesomeIcon icon={['fas', 'home']} />
    </a>

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) =>
    <span className="breadcrumb-last-item">{txt}</span>

  render = () => this.buildBreadCrumb();
}

const mapStateToProps = ({ patient }: IRootState) => ({
  patient: patient.patient
});

const mapDispatchToProps = ({
  getPatient
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(BreadCrumb));
