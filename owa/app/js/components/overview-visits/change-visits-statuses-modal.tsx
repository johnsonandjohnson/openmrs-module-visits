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
import { Modal, Button } from 'react-bootstrap';
import { LocalizedMessage } from '@openmrs/react-components';
import IChangeStatusesModalParams from './change-visits-statuses-modal-param'
import "./change-visits-statuses-modal.scss"

interface IProps {
    show: boolean,
    modalParams: IChangeStatusesModalParams | null,
    confirm: (modalParams: IChangeStatusesModalParams | null) => void,
    cancel: () => void
}

const ChangeVisitsStatusesModal = ({ show, modalParams, confirm, cancel }: IProps) => {
    if (!show && !modalParams) {
      return null;
    }
    
    return (
      <Modal id="change-visits-statuses-modal" show={show} onHide={cancel}>
         <Modal.Body>
           <div className="modal-title">
             <LocalizedMessage
                id="changeVisitsStatuses.modal.title"
                defaultMessage="Change statuses"
              />
           </div>
           <p>
             <LocalizedMessage
                id="changeVisitsStatuses.modal.txt"
                defaultMessage="Please confirm you want to change the status of the selected visit(s)."
              />
           </p>
            <Button
              bsClass="button confirm right"
              onClick={() => confirm(modalParams)}
            >
             <LocalizedMessage
                id="changeVisitsStatuses.modal.confirmLabel"
                defaultMessage="YES"
              />
              </Button>
              <Button bsClass="button cancel" onClick={cancel}>
                  <LocalizedMessage
                     id="changeVisitsStatuses.modal.cancelLabel"
                     defaultMessage="NO"
                   />
              </Button>
              </Modal.Body>
          </Modal>  
    );
  }

export default ChangeVisitsStatusesModal;