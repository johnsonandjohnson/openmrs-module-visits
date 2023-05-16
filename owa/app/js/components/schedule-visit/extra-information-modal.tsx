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
import './extra-information-modal.scss';
import IExtraInformationModalParams from './extra-information-modal-param';
import { formatDateIfDefined, MEDIUM_DATE_FORMAT } from '../../shared/utils/date-util';
import { injectIntl } from 'react-intl';
import { PropsWithIntl } from '../../components/translation/PropsWithIntl';

interface IProps {
	show: boolean,
	modalParams: IExtraInformationModalParams | null,
	locale?: string
	confirm: () => void,
	cancel: () => void
}

const WARNING_CLASS = 'warning';

const ExtraInformationModal = ({ show, modalParams, confirm, cancel, locale, intl }: PropsWithIntl<IProps>) => {
	const isPreviousVisit = !!modalParams?.precedingVisitDaysNumber;
	const isNextVisit = !!modalParams?.nextVistitDaysNumber;
	const isPreviousAndNextVisit = isPreviousVisit && isNextVisit;
	const formattedDate = formatDateIfDefined(MEDIUM_DATE_FORMAT, modalParams?.currentVisitDate);
	const isVisitDateDuplicated = modalParams?.isVisitDateDuplicated;
	
  const currentVisitWeekday = intl.formatMessage({id: `${modalParams?.currentVisitWeekday.toUpperCase()}` })
	const infoMsgPartOne = `${intl.formatMessage({id: "visitSavedOnText" })} ${currentVisitWeekday}, ${formattedDate}.`;
	const infoMsgPartTwo = `${intl.formatMessage({id: "precedingVisitPlannedOnText" })} 
		${modalParams?.precedingVisitDaysNumber} ${intl.formatMessage({id: "daysBeforeText" })}${!isNextVisit ? '.' : ''}`;
	const infoMsgPartThree = `, ${intl.formatMessage({id: "whileText" })}`;
	const nextVisitPlannedText = `${intl.formatMessage({id: "nextVisitPlannedOnText" })}`;
	const nextVisitPlannedConvertedText = !isPreviousAndNextVisit ? nextVisitPlannedText.charAt(0).toUpperCase() + nextVisitPlannedText.slice(1) : nextVisitPlannedText;
	const infoMsgPartFour = ` ${nextVisitPlannedConvertedText} ${modalParams?.nextVistitDaysNumber} ${intl.formatMessage({id: "daysAfterVisitText" })}.`;
	const visitForThisDateIsDuplicatedText = `${intl.formatMessage({id: "duplicatedVisitDateText" })}`;
	
	if (!show) {
		return null;
	}

	return (
		<Modal id="extra-info-modal" show={show} onHide={cancel}>
			<Modal.Body>
				<div className="modal-title">{intl.formatMessage({id: "informationLabel" })}</div>
				<p className={modalParams?.isDayHolidayWeekday ? WARNING_CLASS : ''}>
					{infoMsgPartOne}
					<br />
					{isVisitDateDuplicated && <span>{visitForThisDateIsDuplicatedText}</span>}
				</p>
				<p>
					{isPreviousVisit && <span>{infoMsgPartTwo}</span>}
					{isPreviousAndNextVisit && <span>{infoMsgPartThree}</span>}
					{isNextVisit && <span>{infoMsgPartFour}</span>}
				</p>
				<Button
					bsClass="button confirm right"
					onClick={confirm}
				>
					{intl.formatMessage({id: "okLabel" })}
				</Button>
				<Button bsClass="button cancel" onClick={cancel}>
					{intl.formatMessage({id: "visits.cancelButtonLabel" })}
				</Button>
			</Modal.Body>
		</Modal>
	);
};

export default injectIntl(ExtraInformationModal);