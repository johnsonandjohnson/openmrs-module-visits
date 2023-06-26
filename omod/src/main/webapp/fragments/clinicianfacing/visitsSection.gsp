<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
  ui.includeJavascript("visits", "editVisit.js")
  ui.includeCss("visits", "visitsSection.css")
  def patient = config.patient
%>

<head>
  <!-- Datepicker JS -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/js/bootstrap-datepicker.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap-datepicker@1.9.0/dist/locales/bootstrap-datepicker.${locale}.min.js"></script>
</head>

<span id="locale-span" style="display: none;">${locale}</span>

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

      <div id="edit-visit-dialog" class="dialog">
        <div class="dialog-content">
          <ul>
            <li>
              <label class="dialog-title">${ ui.message("visits.editVisit") } </label>
            </li>
            <li>
              <label class="visit-section-label">${ui.message("visits.visitPlannedDateLabel")}</label>
              <input
                id="visit-date-select"
                type="text"
                onClick="editVisit.handleDateInputOnClick()"
              >
            </li>
            <li>
              <label class="visit-section-label">${ui.message("visits.visitTimeLabel")}</label>
              <select id="visit-time-select" class="required">
                <option value=""></option>
                <% for(visitTime in visitTimes) { %>
                  <option
                    class="selectOption"
                    value="${visitTime}"
                  >
                    ${visitTime}
                  </option>
                <% } %>
              </select>
            </li>
            <li>
              <label class="visit-section-label">
                <span>${ui.message("visits.locationLabel")}</span>
                <span class="required-label">(${ui.message("common.required")})</span>
              </label>
              <select id="visit-location-select" onChange="onSelectChange(this)">
                <option value=""></option>
                <% for(visitLocation in visitLocations) { %>
                  <option
                    class="selectOption"
                    value="${visitLocation.uuid}"
                  >
                    ${visitLocation.name}
                  </option>
                <% } %>
              </select>
              <span id=visit-location-select-error-label class="error-label">${ui.message("common.error.required")}</span>
            </li>
            <li>
              <label class="visit-section-label">
                <span>${ui.message("visits.visitTypeLabel")}</span>
                <span class="required-label">(${ui.message("common.required")})</span>
              </label>
              <select id="visit-type-select" onChange="onSelectChange(this)">
                <option value=""></option>
                <% for(visitType in visitTypes) { %>
                  <option
                    class="selectOption"
                    value="${visitType.uuid}"
                  >
                    ${visitType.name}
                  </option>
                <% } %>
              </select>
              <span id=visit-type-select-error-label class="error-label">${ui.message("common.error.required")}</span>
            </li>
            <li>
              <label class="visit-section-label">
                <span>${ui.message("visits.visitStatusLabel")}</span>
                <span class="required-label">(${ui.message("common.required")})</span>
              </label>
              <select id="visit-status-select" onChange="onSelectChange(this)">
                <option value=""></option>
                <% for(visitStatus in visitStatuses) { %>
                  <option
                    class="selectOption"
                    value="${visitStatus}"
                  >
                    ${visitStatus}
                  </option>
                <% } %>
              </select>
              <span id=visit-status-select-error-label class="error-label">${ui.message("common.error.required")}</span>
            </li>
          </ul>
          <button id="save-button" class="confirm right">
            ${ ui.message("visits.saveButtonLabel") }
            <i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
          </button>
          <button class="cancel">
            ${ ui.message("visits.cancelButtonLabel") }
          </button>
        </div>
      </div>
      <div id="extra-info-dialog" class="dialog">
        <div class="dialog-content">
          <p class="extra-info-dialog-info-label">${ ui.message("informationLabel") }</p>
          </br></br>
          <p class="extra-info-dialog-paragraph">
            <span id="infoMessagePart1"></span>
          </p>
          </br></br>
          <p class="extra-info-dialog-paragraph">
            <span id="infoMessagePart2"></span>
          </p>
          </br></br>
          <button class="confirm right">
            ${ ui.message("okLabel") }
            <i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
          </button>
          <button class="cancel">
            ${ ui.message("visits.cancelButtonLabel") }
          </button>
        </div>
      </div>
      <ul>
         <% recentVisitsWithLinks.each { it, attr -> %>
            <li>
              <span class="visit-date">${attr.visitDateInDisplayFormat}</span>
              <i class="icon-pencil thirty-percent" title=${ui.message("common.edit")}
                onClick="editVisit.showEditVisitDialog(${isExtraInfoDialogEnabled}, '${holidayWeekdays}',
                    '${commaSeparatedVisitDates}', '${attr.visitUuid}', '${patient.uuid}',
                    '${attr.visitDateInServerFormat}', '${attr.visitDetails.time}', '${attr.visitLocationUuid}',
                    '${attr.visitTypeUuid}', '${attr.visitDetails.status}')">
              </i>
              <% if (attr.visitDetails.formUri) { %>
                <a href="../..${attr.visitDetails.formUri}&returnUrl=${ui.urlEncode(ui.thisUrl())}">
                  <i class="icon-stethoscope thirty-percent" title=${ui.message("cfl.visitNote.title")}></i>
                <a/>
              <% } else { %>
                <a>
                  <i class="thirty-percent"}></i>
                <a/>
              <% } %>
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
