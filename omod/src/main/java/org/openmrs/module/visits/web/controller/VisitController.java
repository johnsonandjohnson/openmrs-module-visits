package org.openmrs.module.visits.web.controller;

import org.openmrs.Visit;
import org.openmrs.module.visits.api.dto.PageDTO;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.web.model.PageableParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping("/visits")
public class VisitController extends BaseRestController {

    @Autowired
    @Qualifier("visits.visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("visits.visitMapper")
    private VisitMapper visitMapper;

    @Autowired
    @Qualifier("visits.configService")
    private ConfigService configService;

    @RequestMapping(value = "/times", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVisitTimes() {
        return configService.getVisitTimes();
    }

    @RequestMapping(value = "/statuses", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVisitStatuses() {
        return configService.getVisitStatuses();
    }

    @RequestMapping(value = "/patient/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public PageDTO<VisitDTO> getVisitsForPatient(@PathVariable("uuid") String patientUuid,
                                              PageableParams pageableParams) {
        PagingInfo pagingInfo = pageableParams.getPagingInfo();
        List<Visit> visits = visitService.getVisitsForPatient(patientUuid, pagingInfo);
        return new PageDTO<>(visitMapper.toDtos(visits), pagingInfo);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateVisit(@PathVariable("uuid") String visitUuid, @RequestBody VisitDTO visit) {
        visitService.updateVisit(visitUuid, visit);
    }
}
