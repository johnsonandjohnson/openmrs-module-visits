/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react'
import { Route, Switch } from 'react-router-dom';
import {Header, initializeLocalization, withLocalization} from '@openmrs/react-components';
import BreadCrumb from './components/bread-crumb';
import { ManageVisitsWithHeader as ManageVisits } from './components/hoc/with-patient-header';
import { withNotifications } from './components/hoc/with-notifications';
import OverviewVisits from './components/overview-visits';
import Customize from './components/customize/customize';

import { addLocaleData } from "react-intl";
import pt from 'react-intl/locale-data/pt';
import messagesEN from "./translations/en.json";
import messagesFR from "./translations/fr.json";
import messagesPTBR from "./translations/pt_BR.json";

initializeLocalization({
  en: messagesEN,
  fr: messagesFR,
  pt: messagesPTBR
});
// Add locale data which is not included in @openmrs/react-components
addLocaleData([...pt]);

const LocalizedHeader = withLocalization(Header);
const LocalizedBreadCrumb = withLocalization(BreadCrumb);

export default (store) => (
    <div>
        <Customize />
        <LocalizedHeader />
        <LocalizedBreadCrumb />
        <Switch>
            <Route path="/visits/manage/:patientUuid" component={withNotifications(ManageVisits)} />
            <Route exact path="/visits/overview" component={OverviewVisits} />
        </Switch>
    </div>
);
