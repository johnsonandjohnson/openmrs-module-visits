/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.visits.fragment.controller;

import org.joda.time.DateMidnight;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentryui.fragment.controller.htmlform.EnterHtmlFormFragmentController;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

/**
 * Class copied from htmlformentryui (1.7.0) in order to override the submit action.
 * The one difference is that this version of submit action doesn't validate the encounter datetime.
 */
public class CflEnterHtmlFormFragmentController extends EnterHtmlFormFragmentController {

    @SuppressWarnings({"checkstyle:parameterNumber", "checkstyle:cyclomaticComplexity", "checkstyle:parameterAssignment",
            "PMD.ExcessiveParameterList", "PMD.CyclomaticComplexity", "PMD.NPathComplexity",
            "PMD.AvoidReassigningParameters"})
    @Override
    @Transactional
    public SimpleObject submit(UiSessionContext sessionContext,
            @RequestParam("personId") Patient patient,
            @RequestParam("htmlFormId") HtmlForm hf,
            @RequestParam(value = "encounterId", required = false) Encounter encounter,
            @RequestParam(value = "visitId", required = false) Visit visit,
            @RequestParam(value = "createVisit", required = false) Boolean createVisit,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @SpringBean("adtService") AdtService adtService,
            @SpringBean("featureToggles") FeatureToggleProperties featureToggles,
            UiUtils ui,
            HttpServletRequest request) throws Exception {

        // TODO formModifiedTimestamp and encounterModifiedTimestamp

        boolean editMode = encounter != null;

        FormEntrySession fes;
        if (encounter != null) {
            fes = new FormEntrySession(patient, encounter, FormEntryContext.Mode.EDIT, hf, request.getSession());
        } else {
            fes = new FormEntrySession(patient, hf, FormEntryContext.Mode.ENTER, request.getSession());
        }

        VisitDomainWrapper visitDomainWrapper = getVisitDomainWrapper(visit, encounter, adtService);
        setupVelocityContext(fes, visitDomainWrapper, ui, sessionContext, featureToggles);
        setupFormEntrySession(fes, visitDomainWrapper, ui, sessionContext, returnUrl);
        fes.getHtmlToDisplay();  // needs to happen before we validate or process a form

        // Validate and return with errors if any are found
        List<FormSubmissionError> validationErrors = fes.getSubmissionController().validateSubmission(fes.getContext(),
                request);
        if (validationErrors.size() > 0) {
            return returnHelper(validationErrors, fes, null);
        }

        try {
            // No validation errors found so process form submission
            fes.prepareForSubmit();
            fes.getSubmissionController().handleFormSubmission(fes, request);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            validationErrors.add(new FormSubmissionError("general-form-error", "Form submission error " + ex
                    .getMessage() +
                    "<br/>" + sw.toString()));
            return returnHelper(validationErrors, fes, null);
        }

        // Check this form will actually create an encounter if its supposed to
        if (fes.getContext().getMode() == FormEntryContext.Mode.ENTER && fes.hasEncouterTag()
                && (fes.getSubmissionActions().getEncountersToCreate() == null
                || fes.getSubmissionActions().getEncountersToCreate().size() == 0)) {
            throw new IllegalArgumentException("This form is not going to create an encounter");
        }

        Encounter formEncounter = fes.getContext().getMode() == FormEntryContext.Mode.ENTER
                ? fes.getSubmissionActions().getEncountersToCreate().get(0) : encounter;

        // we don't want to lose any time information just because we edited it with a form that only collects date
        if (fes.getContext().getMode() == FormEntryContext.Mode.EDIT
                && hasNoTimeComponent(formEncounter.getEncounterDatetime())) {
            keepTimeComponentOfEncounterIfDateComponentHasNotChanged(fes.getContext().getPreviousEncounterDate(),
                    formEncounter);
        }

        // create a visit if necessary (note that this currently only works in real-time mode)
        if (createVisit != null && (createVisit) && visit == null) {
            visit = adtService.ensureActiveVisit(patient, sessionContext.getSessionLocation());
            fes.getContext().setVisit(visit);
        }

        // attach to the visit if it exists
        if (visit != null) {
            formEncounter.setVisit(visit);

            if (validationErrors.size() > 0) {
                return returnHelper(validationErrors, fes, null);
            }
        }

        // Do actual encounter creation/updating
        fes.applyActions();

        request.getSession().setAttribute("emr.infoMessage",
                ui.message(editMode ? "htmlformentryui.editHtmlForm.successMessage"
                        : "htmlformentryui.enterHtmlForm.successMessage", ui.format(hf.getForm()),
                        ui.escapeJs(ui.format(patient))));
        request.getSession().setAttribute("emr.toastMessage", "true");

        return returnHelper(null, fes, formEncounter);
    }

    private SimpleObject returnHelper(List<FormSubmissionError> validationErrors, FormEntrySession session,
            Encounter encounter) {
        if (validationErrors == null || validationErrors.size() == 0) {
            String afterSaveUrl = session.getAfterSaveUrlTemplate();
            if (afterSaveUrl != null) {
                afterSaveUrl = afterSaveUrl.replaceAll("\\{\\{patient.id\\}\\}",
                        session.getPatient().getId().toString());
                afterSaveUrl = afterSaveUrl.replaceAll("\\{\\{encounter.id\\}\\}",
                        session.getEncounter().getId().toString());
            }
            return SimpleObject.create("success", true, "encounterId", encounter.getId(), "goToUrl", afterSaveUrl);
        } else {
            Map<String, String> errors = new HashMap<String, String>();
            for (FormSubmissionError err : validationErrors) {
                if (err.getSourceWidget() != null) {
                    errors.put(session.getContext().getErrorFieldId(err.getSourceWidget()), err.getError());
                } else {
                    errors.put(err.getId(), err.getError());
                }
            }
            return SimpleObject.create("success", false, "errors", errors);
        }
    }

    private boolean hasNoTimeComponent(Date date) {
        return new DateMidnight(date).toDate().equals(date);
    }

    private void keepTimeComponentOfEncounterIfDateComponentHasNotChanged(Date previousEncounterDate,
            Encounter formEncounter) {
        if (previousEncounterDate != null
                && new DateMidnight(previousEncounterDate).equals(new DateMidnight(formEncounter.getEncounterDatetime()))) {
            formEncounter.setEncounterDatetime(previousEncounterDate);
        }
    }

    @SuppressWarnings({"checkstyle:parameterAssignment", "PMD.AvoidReassigningParameters"})
    private VisitDomainWrapper getVisitDomainWrapper(Visit visit, Encounter encounter, AdtService adtService) {
        // if we don't have a visit, but the encounter has a visit, use that
        if (visit == null && encounter != null) {
            visit = encounter.getVisit();
        }
        if (visit == null) {
            return null;
        } else {
            return adtService.wrap(visit);
        }
    }
}
