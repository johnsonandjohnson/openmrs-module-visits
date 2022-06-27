<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    def patient = config.patient
%>

<div class="info-section">
    <div class="info-header" style="padding-bottom: 4px;">
        <i class="icon-calendar"></i>
        <h3>${ ui.message("visits.visits").toUpperCase() }</h3>
        <% if (context.hasPrivilege("App: coreapps.patientVisits")) { %>
            <i class="icon-pencil edit-action right" onclick="location.href='${editPageUrl}'" title="${ ui.message("coreapps.edit") }"></i>
        <% } %>
    </div>
    <div class="info-body">
        <% if (recentVisitsWithLinks.isEmpty()) { %>
            ${ui.message("coreapps.none")}
        <% } %>
        <ul>
             <% recentVisitsWithLinks.each { it, attr -> %>
                <li class="clear">
                    <a href="${attr.url}" class="visit-link"></a>
                    <script type="text/javascript">
                        jq('.visit-link[href="${ attr.url }"]')
                            .text(moment.utc("${ it.startDatetime }").local().format('DD MMM YYYY'));
                    </script>
                    <% if (config.showVisitStatus && attr.status) { %>
                       <div class="tag forty-percent">
                           ${ attr.status }
                       </div>
                    <% } %>
                </li>
             <% } %>
        </ul>
    </div>
</div>
