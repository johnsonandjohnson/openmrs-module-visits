<% /* This Source Code Form is subject to the terms of the Mozilla Public License, */ %>
<% /* v. 2.0. If a copy of the MPL was not distributed with this file, You can */ %>
<% /* obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under */ %>
<% /* the terms of the Healthcare Disclaimer located at http://openmrs.org/license. */ %>
<% /* <p> */ %>
<% /* Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS */ %>
<% /* graphic logo is a trademark of OpenMRS Inc. */ %>

<%
    // fragment copied from htmlformentryui (1.7.0) in order to override the submit action. One difference is form action.
    // config supports style (css style on div around form)
    // config supports cssClass (css class on div around form)

    // assumes jquery and jquery-ui from emr module
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js", Integer.MAX_VALUE - 1);
    ui.includeJavascript("htmlformentryui", "dwr-util.js")
    ui.includeJavascript("htmlformentryui", "htmlForm.js")
    ui.includeJavascript("uicommons", "emr.js")
    ui.includeJavascript("uicommons", "moment.js")
%>

<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlForm.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">

    // for now we just expose these in the global scope for compatibility with htmlFormEntry.js and legacy forms
    function submitHtmlForm() {
        htmlForm.submitHtmlForm();
        return false;
    }

    function showDiv(id) {
        htmlForm.showDiv(id);
    }

    function hideDiv(id) {
        htmlForm.hideDiv(id);
    }

    function getValueIfLegal(idAndProperty) {
        htmlForm.getValueIfLegal(idAndProperty);
    }

    function loginThenSubmitHtmlForm() {
        htmlForm.loginThenSubmitHtmlForm();
    }

    var beforeSubmit = htmlForm.getBeforeSubmit();
    var beforeValidation = htmlForm.getBeforeValidation();
    var propertyAccessorInfo = htmlForm.getPropertyAccessorInfo();

    <% if (command.returnUrl) { %>
    htmlForm.setReturnUrl('${ command.returnUrl }');
    <% } %>

    jq(function() {

        // configure the encounter date widget
        // we use this convoluted approach because:
        // 1) if we don't strip off the time component, and the client time zone is different than the server time zone, the client will convert to it's time zone when parsing (potentially leading to the wrong date)
        // 2) if we strip off the time component, but just do a straight new Date("2014-05-05"), some browsers will interpret the time zone as UTC and convert (again potentially leading to the wrong date)
        // so we use moment, which uses the local time zone when parsing "2014-05-05" and then convert back to a date object (toDate()) since that is what the set method is expecting


        <% if (visit) { %>
        <% if (command.context.mode.toString().equals('ENTER') && !visit.isOpen()) { %>
        // set default date to the visit start date for retrospective visits
        htmlForm.setEncounterDate(moment('${ ui.dateToISOString(visit.startDate).split('T')[0] }').toDate());
        <% } %>

        // set valid date range based on visit
        htmlForm.setEncounterStartDateRange(moment('${  ui.dateToISOString(visit.startDate).split('T')[0] }').toDate());
        htmlForm.setEncounterStopDateRange(moment('${ visit.stopDate ? ui.dateToISOString(visit.stopDate).split('T')[0] : ui.dateToISOString(currentDate).split('T')[0] }').toDate());

        <% } else { %>
        // note that we need to get the current datetime from the *server*, in case the server and client are in different time zones
        htmlForm.setEncounterStopDateRange(moment('${  ui.dateToISOString(currentDate).split('T')[0] }').toDate());
        htmlForm.setEncounterDate(moment('${  ui.dateToISOString(currentDate).split('T')[0] }').toDate());
        <% } %>

        // for now, just disable manual entry until we figure out proper validation
        htmlForm.disableEncounterDateManualEntry();

    });

    jq(document).ready(function() {
        jQuery.each(jq("htmlform").find('input'), function(){
            jq(this).bind('keypress', function(e){
                if (e.keyCode == 13) {
                    if (!jq(this).hasClass("submitButton")) {
                        e.preventDefault();
                    }
                }
            });
        });

        // performs any initialization required by the "htmlForm" object (which is defined in htmlForm.js in the htmlformentry module and extended by htmlForm.js in this module)
        htmlForm.initialize();
    });

</script>

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %> <% if (config.cssClass) { %>class="${config.cssClass}"<% } %>>

    <span class="error" style="display: none" id="general-form-error"></span>

    <form id="htmlform" method="post" action="${ ui.actionLink("visits", "visitEnterHtmlForm", "submit") }" onSubmit="submitHtmlForm(); return false;">
        <input type="hidden" name="personId" value="${ command.patient.personId }"/>
        <input type="hidden" name="htmlFormId" value="${ command.htmlFormId }"/>
        <input type="hidden" name="createVisit" value="${ createVisit }"/>
        <input type="hidden" name="formModifiedTimestamp" value="${ command.formModifiedTimestamp }"/>
        <input type="hidden" name="encounterModifiedTimestamp" value="${ command.encounterModifiedTimestamp }"/>
        <% if (command.encounter) { %>
        <input type="hidden" name="encounterId" value="${ command.encounter.encounterId }"/>
        <% } %>
        <% if (visit) { %>
        <input type="hidden" name="visitId" value="${ visit.visitId }"/>
        <% } %>
        <% if (command.returnUrl) { %>
        <input type="hidden" name="returnUrl" value="${ command.returnUrl }"/>
        <% } %>
        <input type="hidden" name="closeAfterSubmission" value="${ config.closeAfterSubmission }"/>

        ${ command.htmlToDisplay }

        <div id="passwordPopup" style="position: absolute; z-axis: 1; bottom: 25px; background-color: #ffff00; border: 2px black solid; display: none; padding: 10px">
            <center>
                <table>
                    <tr>
                        <td colspan="2"><b>${ ui.message("htmlformentry.loginAgainMessage") }</b></td>
                    </tr>
                    <tr>
                        <td align="right"><b>${ ui.message("coreapps.user.username") }:</b></td>
                        <td><input type="text" id="passwordPopupUsername"/></td>
                    </tr>
                    <tr>
                        <td align="right"><b>${ ui.message("coreapps.user.password") }:</b></td>
                        <td><input type="password" id="passwordPopupPassword"/></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="center"><input type="button" value="Submit" onClick="loginThenSubmitHtmlForm()"/></td>
                    </tr>
                </table>
            </center>
        </div>
    </form>
</div>

<% if (command.fieldAccessorJavascript) { %>
<script type="text/javascript">
    ${ command.fieldAccessorJavascript }
</script>
<% } %>
