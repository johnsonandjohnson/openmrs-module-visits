/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react'
import { Route, Switch } from 'react-router-dom';
import { Header } from '@openmrs/react-components';
import App from './components/app'
import BreadCrumb from './components/bread-crumb';
import { ScheduleVisitWithHeader as ScheduleVisit } from './components/hoc/with-patient-header';
import { ManageVisitsWithHeader as ManageVisits } from './components/hoc/with-patient-header';

export default (store) => (    
    <div>
        <Header />
        <BreadCrumb />
        <Switch>
            <Route path="/visits/manage/:patientUuid/schedule/:visitUuid?" component={ScheduleVisit} />
            <Route path="/visits/manage/:patientUuid" component={ManageVisits} />
        </Switch>
    </div>
);
