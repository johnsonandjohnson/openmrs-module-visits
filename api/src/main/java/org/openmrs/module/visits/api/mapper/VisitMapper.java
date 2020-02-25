package org.openmrs.module.visits.api.mapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.exception.ValidationException;

public final class VisitMapper extends AbstractMapper<VisitDTO, Visit> {

    private static final Log LOGGER = LogFactory.getLog(VisitMapper.class);

    private VisitService visitService;

    private LocationService locationService;

    @Override
    public VisitDTO toDto(Visit visit) {
        VisitDecorator visitDecorator = new VisitDecorator(visit);
        String stringUri = null;
        try {
            stringUri = VisitFormUriHelper.getVisitFormUri(visit.getPatient(), visit);
        } catch (ValidationException e) {
            LOGGER.error(e.getErrorResponse().getError());
        }

        return new VisitDTO()
                .setUuid(visit.getUuid())
                .setStartDate(visit.getStartDatetime())
                .setTime(visitDecorator.getTime())
                .setLocation(visit.getLocation() == null ? null : visit.getLocation().getName())
                .setType(visit.getVisitType() == null ? null : visit.getVisitType().getName())
                .setStatus(visitDecorator.getStatus())
                .setFormUri(stringUri);
    }

    @Override
    public Visit fromDto(VisitDTO dto) {
        Visit result = visitService.getVisitByUuid(dto.getUuid());
        if (result == null) {
            result = new Visit();
        }
        VisitDecorator resultDecorator = new VisitDecorator(result);
        resultDecorator.setStartDatetime(dto.getStartDate());
        resultDecorator.setLocation(locationService.getLocationByUuid(dto.getLocation()));
        resultDecorator.setVisitType(visitService.getVisitTypeByUuid(dto.getType()));
        resultDecorator.setStatus(dto.getStatus());
        resultDecorator.setTime(dto.getTime());
        return resultDecorator.getObject();
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
