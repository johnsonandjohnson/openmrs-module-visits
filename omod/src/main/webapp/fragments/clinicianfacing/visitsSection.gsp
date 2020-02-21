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
                    <a href="${attr.url}" class="visit-link">
                        ${ ui.formatDatePretty(it.startDatetime) }
                    </a>
                    <% if (config.showVisitStatus) { %>
                       <div class="tag forty-percent">
                           ${ attr.status }
                       </div>
                    <% } %>
                </li>
             <% } %>
        </ul>
    </div>
</div>
