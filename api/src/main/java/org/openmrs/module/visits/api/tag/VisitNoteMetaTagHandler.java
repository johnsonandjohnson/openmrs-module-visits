package org.openmrs.module.visits.api.tag;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.VisitService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.visits.api.util.GlobalPropertiesConstants.STATUSES_ENDING_VISIT;

public class VisitNoteMetaTagHandler extends SubstitutionTagHandler {

    private static final Log LOGGER = LogFactory.getLog(VisitNoteMetaTagHandler.class);
    private static final String VISIT_SERVICE = "visits.visitService";
    private static final String VISIT_MAPPER = "visits.visitMapper";

    @Override
    protected List<AttributeDescriptor> createAttributeDescriptors() {
        List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>();
        return Collections.unmodifiableList(attributeDescriptors);
    }

    @Override
    protected String getSubstitution(FormEntrySession formEntrySession, FormSubmissionController formSubmissionController,
                                     Map<String, String> attributes) throws BadFormDesignException {
        Element element = new Element();
        formSubmissionController.addAction(element);

        return element.generateHtml(formEntrySession.getContext());
    }

    class Element implements FormSubmissionControllerAction, HtmlGeneratorElement {

        @Override
        public String generateHtml(FormEntryContext context) {
            return StringUtils.EMPTY;
        }

        @Override
        public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest request) {
            return null;
        }

        @Override
        public void handleSubmission(FormEntrySession formEntrySession, HttpServletRequest request) {
            if (formEntrySession.getContext().getVisit() != null) {
                Visit visit = (Visit) formEntrySession.getContext().getVisit();
                getVisitMapper().setVisitStatus(visit, STATUSES_ENDING_VISIT.getDefaultValue());
                getVisitService().saveOrUpdate(visit);
                LOGGER.info(String.format("Visit with uuid: %s has successfully changed the status.", visit.getUuid()));
            }
        }

        private VisitService getVisitService() {
            return Context.getRegisteredComponent(VISIT_SERVICE, VisitService.class);
        }

        private VisitMapper getVisitMapper() {
            return Context.getRegisteredComponent(VISIT_MAPPER, VisitMapper.class);
        }
    }
}
