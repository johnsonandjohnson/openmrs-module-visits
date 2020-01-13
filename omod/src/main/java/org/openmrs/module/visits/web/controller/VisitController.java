package org.openmrs.module.visits.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_TIMES_KEY;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_TIMES_SEPARATOR;

@Controller
@RequestMapping("/visits")
public class VisitController extends BaseRestController {

    @RequestMapping(value = "/times", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVisitTimes() {
        String visitTimesProperty = Context.getAdministrationService().getGlobalProperty(VISIT_TIMES_KEY);
        if (StringUtils.isBlank(visitTimesProperty)) {
            return new ArrayList<>();
        }
        return Arrays.asList(visitTimesProperty.split(VISIT_TIMES_SEPARATOR));
    }
}
