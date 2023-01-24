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
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

interface IProps {
	show: boolean,
	modalParams: IExtraInformationModalParams | null,
	locale?: string
	confirm: () => void,
	cancel: () => void
}

const WARNING_CLASS = 'warning';

const ExtraInformationModal = ({ show, modalParams, confirm, cancel, locale }: IProps) => {
	const isPreviousVisit = !!modalParams?.precedingVisitDaysNumber;
	const isNextVisit = !!modalParams?.nextVistitDaysNumber;
	const isPreviousAndNextVisit = isPreviousVisit && isNextVisit;
	const formattedDate = formatDateIfDefined(MEDIUM_DATE_FORMAT, modalParams?.currentVisitDate);
	
  const currentVisitWeekday = getIntl(locale).formatMessage({id: `${modalParams?.currentVisitWeekday.toUpperCase()}` })
	const infoMsgPartOne = `${getIntl(locale).formatMessage({id: "VISIT_SAVED_ON_TEXT", defaultMessage: "The visit will be saved on"})} ${currentVisitWeekday}, ${formattedDate}.`;
	const infoMsgPartTwo = `${getIntl(locale).formatMessage({id: "PRECEDING_VISIT_PLANNED_ON_TEXT", defaultMessage: "The preceding visit is planned"})} 
		${modalParams?.precedingVisitDaysNumber} ${getIntl(locale).formatMessage({id: "DAYS_BEFORE_TEXT", defaultMessage: "day(s) before"})}${!isNextVisit ? '.' : ''}`;
	const infoMsgPartThree = `, ${getIntl(locale).formatMessage({id: "WHILE_TEXT", defaultMessage: ", while"})}`;
	const nextVisitPlannedText = `${getIntl(locale).formatMessage({id: "NEXT_VISIT_PLANNED_ON_TEXT", defaultMessage: "the next visit is planned"})}`;
	const nextVisitPlannedConvertedText = !isPreviousAndNextVisit ? nextVisitPlannedText.charAt(0).toUpperCase() + nextVisitPlannedText.slice(1) : nextVisitPlannedText;
	const infoMsgPartFour = ` ${nextVisitPlannedConvertedText} ${modalParams?.nextVistitDaysNumber} ${getIntl(locale).formatMessage({id: "DAYS_AFTER_VISIT_TEXT", defaultMessage: "day(s) after the visit"})}.`;
	
	if (!show) {
		return null;
	}

	return (
		<Modal id="extra-info-modal" show={show} onHide={cancel}>
			<Modal.Body>
				<div className="modal-title">{getIntl(locale).formatMessage({id: "INFORMATION_LABEL", defaultMessage: "Information"})}</div>
				<p className={modalParams?.isDayHolidayWeekday ? WARNING_CLASS : ''}>{infoMsgPartOne}</p>
				<p>
					{isPreviousVisit && <span>{infoMsgPartTwo}</span>}
					{isPreviousAndNextVisit && <span>{infoMsgPartThree}</span>}
					{isNextVisit && <span>{infoMsgPartFour}</span>}
				</p>
				<Button
					bsClass="button confirm right"
					onClick={confirm}
				>
					{getIntl(locale).formatMessage({id: "OK", defaultMessage: "OK"})}
				</Button>
				<Button bsClass="button cancel" onClick={cancel}>
					{getIntl(locale).formatMessage({id: "VISITS_CANCEL_BUTTON_LABEL", defaultMessage: "Cancel"})}
				</Button>
			</Modal.Body>
		</Modal>
	);
};

export default ExtraInformationModal;