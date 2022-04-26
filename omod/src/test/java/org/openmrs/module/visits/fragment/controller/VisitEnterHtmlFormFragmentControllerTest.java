package org.openmrs.module.visits.fragment.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, HtmlFormEntryUtil.class, SimpleObject.class})
public class VisitEnterHtmlFormFragmentControllerTest {

  private static final String HTML_FORM_XML_DEFINITION =
      "<htmlform>\n"
          + "\t<macros>\n"
          + "\t\tpaperFormId = (Fill this in)\n"
          + "\t\theaderColor =#009d8e\n"
          + "\t\tfontOnHeaderColor = white\n"
          + "\t</macros>\n"
          + "\t<h2>Test form</h2>\n"
          + "\t<section headerLabel=\"1. Encounter Details\">\n"
          + "\t\t<table class=\"baseline-aligned\">\n"
          + "\t\t\t<tr>\n"
          + "\t\t\t\t<td>Date:</td>\n"
          + "\t\t\t\t<td><encounterDate default=\"today\"/></td>\n"
          + "\t\t\t</tr>\n"
          + "\t\t</table>\n"
          + "\t</section>\n"
          + "\t<submit/>\n"
          + "</htmlform>";

  private static final Date NOW = new Date();

  private final VisitEnterHtmlFormFragmentController controller =
      new VisitEnterHtmlFormFragmentController();

  @Mock private UiSessionContext uiSessionContext;

  @Mock private Patient patient;

  @Mock private HtmlForm htmlForm;

  @Mock private Encounter encounter;

  @Mock private Visit visit;

  @Mock private Boolean createVisit;

  @Mock private String returnUrl;

  @Mock private AdtService adtService;

  @Mock private FeatureToggleProperties featureToggleProperties;

  @Mock private UiUtils uiUtils;

  @Mock private HttpServletRequest httpServletRequest;

  @Mock private HtmlFormEntryService htmlFormEntryService;

  @Mock private Document document;

  @Mock private Node node;

  @Mock private NodeList nodeList;

  @Mock private AdministrationService administrationService;

  @Mock private EncounterService encounterService;

  @Mock private HttpSession httpSession;

  @Mock private FormEntrySession formEntrySession;

  @Mock private Form form;

  @Before
  public void setUp() throws Exception {
    mockStatic(Context.class);
    mockStatic(HtmlFormEntryUtil.class);
    mockStatic(SimpleObject.class);

    when(Context.getAuthenticatedUser()).thenReturn(new User(1));
    when(HtmlFormEntryUtil.getService()).thenReturn(htmlFormEntryService);
    when(HtmlFormEntryUtil.stringToDocument(anyString())).thenReturn(document);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(Context.getEncounterService()).thenReturn(encounterService);
    when(htmlForm.getDateChanged()).thenReturn(new Date());
    when(encounter.getDateCreated()).thenReturn(NOW);
    when(encounter.getDateChanged()).thenReturn(NOW);
    when(htmlForm.getXmlData()).thenReturn(HTML_FORM_XML_DEFINITION);
    when(HtmlFormEntryUtil.findChild(document, "htmlform")).thenReturn(node);
    when(node.getChildNodes()).thenReturn(nodeList);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(formEntrySession.getAfterSaveUrlTemplate()).thenReturn("testString");
    when(formEntrySession.getPatient()).thenReturn(patient);
    when(formEntrySession.getEncounter()).thenReturn(encounter);
    when(encounter.getId()).thenReturn(100);
  }

  @Test
  public void shouldSetAppropriateSessionAttributeWhenAllPassedObjectsAreNotNull()
      throws Exception {
    controller.submit(
        uiSessionContext,
        patient,
        htmlForm,
        encounter,
        visit,
        createVisit,
        returnUrl,
        adtService,
        featureToggleProperties,
        uiUtils,
        httpServletRequest);

    verify(httpSession).setAttribute(eq("emr.infoMessage"), anyString());
    verify(httpSession).setAttribute("emr.toastMessage", "true");
  }

  @Test(expected = BadFormDesignException.class)
  public void shouldThrowBadFormDesignExceptionWhenEncounterIsBlank() throws Exception {
    when(htmlForm.getForm()).thenReturn(form);

    controller.submit(
        uiSessionContext,
        patient,
        htmlForm,
        null,
        visit,
        createVisit,
        returnUrl,
        adtService,
        featureToggleProperties,
        uiUtils,
        httpServletRequest);
  }

  @Test
  public void shouldSetAppropriateSessionAttributeWhenVisitIsNull() throws Exception {
    when(encounter.getEncounterDatetime())
        .thenReturn(
            Date.from(
                new Date()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()));

    controller.submit(
        uiSessionContext,
        patient,
        htmlForm,
        encounter,
        null,
        true,
        returnUrl,
        adtService,
        featureToggleProperties,
        uiUtils,
        httpServletRequest);

    verify(httpSession).setAttribute(eq("emr.infoMessage"), anyString());
    verify(httpSession).setAttribute("emr.toastMessage", "true");
  }

  @Test
  public void shouldRegisterSuccessWhenNoValidationErrors() {
    controller.getValidationResults(
        prepareFormSubmissionErrorsList(0), formEntrySession, encounter);

    verifyStatic();
    SimpleObject.create("success", Boolean.TRUE, "encounterId", 100, "goToUrl", "testString");
  }

  @Test
  public void shouldNotRegisterSuccessWhenValidationErrorsOccur() {
    controller.getValidationResults(
        prepareFormSubmissionErrorsList(3), formEntrySession, encounter);

    verifyStatic();
    SimpleObject.create(
        eq("success"), eq(Boolean.FALSE), eq("errors"), anyListOf(FormSubmissionError.class));
  }

  private List<FormSubmissionError> prepareFormSubmissionErrorsList(int numberOfErrors) {
    List<FormSubmissionError> errorList = new ArrayList<>();
    for (int i = 0; i < numberOfErrors; i++) {
      errorList.add(new FormSubmissionError(String.valueOf(i), "error" + i));
    }
    return errorList;
  }
}
