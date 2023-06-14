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
import { injectIntl } from 'react-intl';
import { PropsWithIntl } from "../../translation/PropsWithIntl";
import { IRootState } from "../../../reducers";
import { connect } from "react-redux";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashAlt } from "@fortawesome/free-regular-svg-icons";
import DeleteVisitModal from "../../manage-visits/delete-visit-modal";
import IModalParams from "../../manage-visits/modal-params";
import { deleteVisitWithCallback } from "../../../reducers/schedule-visit.reducer";

interface IDeleteRowActionProps extends DispatchProps, StateProps {
  viewIndex: number
  visitUuid: string
  refreshTableCallback: () => void;
}

interface IDeleteRowActionState {
  showDeleteVisitModal: boolean
  modalParams: IModalParams | null
}

const DEFAULT_STATE = {
  showDeleteVisitModal: false,
  modalParams: null
};

class DeleteRowAction extends React.Component<PropsWithIntl<IDeleteRowActionProps>, IDeleteRowActionState> {
  state = DEFAULT_STATE;

  private handleOnClickDeleteAction = () => {
    this.setState({ showDeleteVisitModal: true, modalParams: { uuid: this.props.visitUuid } as IModalParams });
  };

  private closeDeleteVisitModal = () => this.setState(DEFAULT_STATE);

  private confirmDeleteVisit = (modalParams: IModalParams | null) => {
    if (!!modalParams) {
      this.props.deleteVisitWithCallback(this.props.visitUuid, this.props.intl, this.props.refreshTableCallback);
      this.closeDeleteVisitModal();
    }
  };

  render = () => {
    return (
      <>
        <span className="action-button">
          <i
            id={`visit-delete-button-${this.props.viewIndex}`}
            onClick={this.handleOnClickDeleteAction}
          >
            <FontAwesomeIcon icon={faTrashAlt} size="1x"/>
          </i>
        </span>
        <DeleteVisitModal
          show={this.state.showDeleteVisitModal}
          modalParams={this.state.modalParams}
          confirm={this.confirmDeleteVisit}
          cancel={this.closeDeleteVisitModal}
        />
      </>
    )
  }
}

const mapStateToProps = ({}: IRootState) => ({});
const mapDispatchToProps = ({ deleteVisitWithCallback });

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default injectIntl(connect(
  mapStateToProps,
  mapDispatchToProps
)(DeleteRowAction));
