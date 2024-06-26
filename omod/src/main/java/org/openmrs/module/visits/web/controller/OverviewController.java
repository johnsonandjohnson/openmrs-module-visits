/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.dto.OverviewDTO;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.api.service.VisitSimpleQuery;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.dto.PageDTO;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.visits.web.model.PageableParams;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exposes the endpoints related to Visit Overview
 */
@Api(value = "Visit Overview", tags = {"REST API for visit overview"})
@Controller
@RequestMapping("/visits/overview")
@SuppressWarnings("CPD-START")
public class OverviewController extends BaseRestController {

  @Autowired
  @Qualifier("visits.visitService")
  private VisitService visitService;

  /**
   * Fetches the page of the visits for given location
   *
   * @param locationUuid   uuid of the location
   * @param pageableParams parameters representing expected page shape
   * @param query          optional query containing phrase filtering the visits by patient's
   *                       identifier or name, eg. phrase 'jo' may result in returning the visits
   *                       for patient 'John'
   * @param visitStatus    used for filtering visits by visit status
   * @param dateFrom       used for filtering visits where planned date of visit is greater or
   *                       equals than dateFrom
   * @param dateTo         used for filtering visit where planned date of visit is less or equals
   *                       than dateTo
   * @param timePeriod     used for filtering visits depending on value from
   *                       {@link org.openmrs.module.visits.api.model.TimePeriod}. If value is not
   *                       provided, default value = TODAY is used.
   * @return a page containing visit overview details
   */
  @ApiOperation(value = "Get visits for location", notes = "Get visits for location", response = OverviewDTO.class)
  @ApiResponses(value = {@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "visits for location retrieved"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Visits for location not retrieved"),
      @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Location not found")})
  @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
  @ResponseBody
  public PageDTO<SimpleObject> getVisitsForLocation(
      @ApiParam(name = "uuid", value = "uuid", required = true) @PathVariable("uuid") String locationUuid,
      PageableParams pageableParams,
      @ApiParam(name = "patientUuid", value = "patientUuid") @RequestParam(required = false) String patientUuid,
      @ApiParam(name = "query", value = "query") @RequestParam(required = false) String query,
      @ApiParam(name = "visitStatus", value = "visitStatus") @RequestParam(required = false) String visitStatus,
      @ApiParam(name = "dateFrom", value = "dateFrom") @RequestParam(required = false) Long dateFrom,
      @ApiParam(name = "dateTo", value = "dateTo") @RequestParam(required = false) Long dateTo,
      @ApiParam(name = "timePeriod", value = "timePeriod") @RequestParam(required = false) String timePeriod) {
    return getVisits(pageableParams, locationUuid, patientUuid, query, visitStatus, dateFrom, dateTo, timePeriod,
        VisitSimpleQuery.SORT_DESCENDING);
  }

  @ApiOperation(value = "Get visits", notes = "Get visits", response = OverviewDTO.class)
  @ApiResponses(value = {@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "visits for location retrieved"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Visits not retrieved")})
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseBody
  public PageDTO<SimpleObject> getVisits(PageableParams pageableParams,
                                         @ApiParam(name = "uuid", value = "uuid", required = true)
                                         @RequestParam(required = false) String locationUuid,
                                         @ApiParam(name = "patientUuid", value = "patientUuid")
                                         @RequestParam(required = false) String patientUuid,
                                         @ApiParam(name = "query", value = "query") @RequestParam(required = false)
                                             String query, @ApiParam(name = "visitStatus", value = "visitStatus")
                                         @RequestParam(required = false) String visitStatus,
                                         @ApiParam(name = "dateFrom", value = "dateFrom") @RequestParam(required = false)
                                             Long dateFrom,
                                         @ApiParam(name = "dateTo", value = "dateTo") @RequestParam(required = false)
                                             Long dateTo,
                                         @ApiParam(name = "timePeriod", value = "timePeriod") @RequestParam(required = false)
                                             String timePeriod, @ApiParam(name = "startDatetimeSort", value = "startDatetimeSort")
                                         @RequestParam(required = false) String startDatetimeSort) {

    final PagingInfo pagingInfo = pageableParams.getPagingInfo();
    final VisitSimpleQuery visitForLocationQuery = new VisitSimpleQuery.Builder()
        .withLocationUuid(locationUuid)
        .withPatientUuid(patientUuid)
        .withPagingInfo(pagingInfo)
        .withQuery(query)
        .withVisitStatus(visitStatus)
        .withDateFrom(dateFrom)
        .withDateTo(dateTo)
        .withTimePeriod(timePeriod)
        .withDtartDatetimeSort(startDatetimeSort)
        .build();

    final List<Visit> visits = visitService.getVisits(visitForLocationQuery);

    final BaseDelegatingResource<Visit> visitResource =
        (BaseDelegatingResource<Visit>) Context.getService(RestService.class).getResourceBySupportedClass(Visit.class);
    final List<SimpleObject> visitDTOs = visits
        .stream()
        .map(visit -> visitResource.asRepresentation(visit,
            new NamedRepresentation(VisitsRestConstants.OVERVIEW_VISIT_REPRESENTATION)))
        .collect(Collectors.toList());

    return new PageDTO<>(visitDTOs, pagingInfo);
  }

  @ApiOperation(value = "Updates visits with new status", notes = "Updates visits with new status")
  @ApiResponses(value = {@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Visits statuses changed successfully"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR,
          message = "An error occurred while updating visit statuses")})
  @RequestMapping(value = "/updateVisitStatuses", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void updateVisitStatuses(
      @ApiParam(name = "visitUuids", value = "visitUuids", required = true) @RequestBody List<String> visitUuids,
      @ApiParam(name = "newVisitStatus", value = "newVisitStatus", required = true) @RequestParam String newVisitStatus) {
    visitService.changeVisitStatuses(visitUuids, newVisitStatus);
  }
}
