/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.fragment.controller.clinicianfacing;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.template.TemplateFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.utils.VisitTypeHelper;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.util.GlobalPropertyUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, GlobalPropertyUtils.class})
public class VisitsSectionFragmentControllerTest {

  private final VisitsSectionFragmentController controller = new VisitsSectionFragmentController();

  private static final String PATIENT_ATTR_NAME = "patient";
  
  @Mock private TemplateFactory templateFactory;

  @Mock private AdtService adtService;

  @Mock private VisitTypeHelper visitTypeHelper;

  @Mock private CoreAppsProperties coreAppsProperties;

  @Mock private FragmentConfiguration configuration;

  @Mock private FragmentModel model;

  @Mock private UiUtils uiUtils;

  @Mock private UiSessionContext uiSessionContext;

  @Mock private PatientDomainWrapper patientDomainWrapper;

  @Mock private Patient patient;

  @Mock private AppContextModel appContextModel;

  @Mock private ConfigService configService;

  @Mock private VisitService visitService;

  @Mock private LocationService locationService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    mockStatic(GlobalPropertyUtils.class);

    when(Context.getRegisteredComponent("appframeworkTemplateFactory", TemplateFactory.class))
        .thenReturn(templateFactory);
    when(Context.getService(AdtService.class)).thenReturn(adtService);
    when(Context.getRegisteredComponent("visitTypeHelper", VisitTypeHelper.class))
        .thenReturn(visitTypeHelper);
    when(Context.getRegisteredComponent("coreAppsProperties", CoreAppsProperties.class))
        .thenReturn(coreAppsProperties);
    when(patientDomainWrapper.getPatient()).thenReturn(new Patient(1));
    doReturn(appContextModel).when(uiSessionContext).generateAppContextModel();
    when(GlobalPropertyUtils.getInteger(anyString())).thenReturn(1);
    when(Context.getService(ConfigService.class)).thenReturn(configService);
    when(Context.getVisitService()).thenReturn(visitService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getLocale()).thenReturn(Locale.ENGLISH);
  }

  @Test
  public void shouldPrepareControllerWhenPatientIsPatientTypeObject() {
    doReturn(patient).when(configuration).get(anyString());

    controller.controller(configuration, model, uiUtils, uiSessionContext, patientDomainWrapper);

    verifyBasicProperties();
    verify(configuration).addAttribute(eq(PATIENT_ATTR_NAME), any(PatientDomainWrapper.class));
  }

  @Test
  public void shouldPrepareControllerWhenPatientIsPatientDomainWrapperTypeObject() {
    doReturn(patientDomainWrapper).when(configuration).get(anyString());

    controller.controller(configuration, model, uiUtils, uiSessionContext, patientDomainWrapper);

    verifyBasicProperties();
  }

  @Test
  public void shouldPrepareControllerWhenVisitsPageUrlContainsPatientDashboardUrlSuffix() {
    doReturn(patient).when(configuration).get(anyString());

    controller.controller(configuration, model, uiUtils, uiSessionContext, patientDomainWrapper);

    verifyBasicProperties();
    verify(configuration).addAttribute(eq(PATIENT_ATTR_NAME), any(PatientDomainWrapper.class));
  }

  @Test
  public void shouldAddVisitParamToModelWhenActiveVisitIsAvailable() {
    doReturn("testUrl").when(configuration).get("visitUrl");
    doReturn("/coreapps/patientdashboard/patientDashboard.page?")
        .when(configuration)
        .get("editPageUrl");
    when(adtService.getActiveVisit(any(Patient.class), any(Location.class)))
        .thenReturn(new VisitDomainWrapper());

    controller.controller(configuration, model, uiUtils, uiSessionContext, patientDomainWrapper);

    verify(appContextModel).put(eq("visit"), any(Visit.class));
  }

  @Test
  public void shouldAddVisitParamTwiceWhenActiveVisitAndVisitsToDisplayAreNotEmpty() {
    doReturn("testUrl").when(configuration).get("visitUrl");
    doReturn("/coreapps/patientdashboard/patientDashboard.page?")
        .when(configuration)
        .get("editPageUrl");
    when(adtService.getActiveVisit(any(Patient.class), any(Location.class)))
        .thenReturn(new VisitDomainWrapper());

    controller.controller(configuration, model, uiUtils, uiSessionContext, patientDomainWrapper);

    verifyBasicProperties();
    verify(appContextModel, times(1)).put(eq("visit"), any(Visit.class));
  }

  private void verifyBasicProperties() {
    verify(appContextModel).put(eq(PATIENT_ATTR_NAME), any(PatientContextModel.class));
    verify(appContextModel).put(eq("patientId"), any(String.class));
    verify(model).addAttribute(eq("recentVisitsWithLinks"), anyCollection());
    verify(configuration).addAttribute("showVisitTypeOnPatientHeaderSection", false);
  }
}
