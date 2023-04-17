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
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import { injectIntl } from 'react-intl';
import { connect } from "react-redux";
import { getVisit, getVisitsPage, updateVisit } from "../../../reducers/schedule-visit.reducer";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencilAlt } from "@fortawesome/free-solid-svg-icons";
import IVisit from "../../../shared/model/visit";

interface IEditVisitRowActionProps extends DispatchProps, StateProps {
  viewIndex: number
  visit: IVisit
  openScheduleVisitModalCallback: (visitUuid: string | null) => void
}

interface IEditVisitActionState {

}

class EditVisitAction extends React.Component<PropsWithIntl<IEditVisitRowActionProps>, IEditVisitActionState> {
  render = () => {
    const visit = this.props.visit;
    return (
      <>
        <span className="action-button">
          <i
            id={`visit-update-button-${this.props.viewIndex}`}
            onClick={() => this.props.openScheduleVisitModalCallback(visit.uuid)}
          >
            <FontAwesomeIcon icon={faPencilAlt} size="1x"/>
          </i>
        </span>
      </>
    )
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({ updateVisit, getVisit, getVisitsPage });

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(EditVisitAction));
