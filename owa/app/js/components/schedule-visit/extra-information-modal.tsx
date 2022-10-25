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
import './extra-information-modal.scss';
import IExtraInformationModalParams from './extra-information-modal-param';
import { formatDateIfDefined, MEDIUM_DATE_FORMAT } from '../../shared/utils/date-util';

interface IProps {
	show: boolean,
	modalParams: IExtraInformationModalParams | null,
	confirm: () => void,
	cancel: () => void
}

interface IState {
}

const WARNING_CSS_CLASS = 'warning-color';

class ExtraInformationModal extends React.PureComponent<IProps, IState> {
	
	buildModal = () => {
		const { show, modalParams, confirm, cancel } = this.props;

		const formattedDate = formatDateIfDefined(MEDIUM_DATE_FORMAT, modalParams?.currentVisitDate);

		const infoMsgPartOne = 'The visit will be saved on ' + modalParams?.currentVisitWeekday + ", " + formattedDate + '.';
		const infoMsgPartTwo = 'The preceding visit is planned ' + modalParams?.precedingVisitDaysNumber + 
			' day(s) before, while the next visit is planned ' + modalParams?.nextVistitDaysNumber + ' day(s) after the visit.';
		const isPreviousAndNextVisit = !!modalParams?.precedingVisitDaysNumber && !!modalParams?.nextVistitDaysNumber;

		const title = <LocalizedMessage
			id="extraInfo.modal.title"
			defaultMessage="Information" />;
		const confirmLabel = <LocalizedMessage
			id="extraInfo.modal.confirmLabel"
			defaultMessage="OK" />;
		const cancelLabel = <LocalizedMessage
			id="extraInfo.modal.cancelLabel"
			defaultMessage="Cancel" />;

			return (
				<Modal id="extra-info-modal" show={show} onHide={cancel}>
					<Modal.Body>
						<div className="modal-title">{title}</div>
						<p className={modalParams?.isDayHolidayWeekday ? WARNING_CSS_CLASS : ''}>{infoMsgPartOne}</p>
						{isPreviousAndNextVisit && <p>{infoMsgPartTwo}</p>}
						<Button
							bsClass="button confirm right"
							onClick={() => {
								confirm();
							}}
						>
							{confirmLabel}
						</Button>
						<Button bsClass="button cancel" onClick={this.props.cancel}>
							{cancelLabel}
						</Button>
					</Modal.Body>
				</Modal>
			);
	}

	render() {
    return (
      this.props.show ? this.buildModal(): null
    );
  };
}

export default ExtraInformationModal;