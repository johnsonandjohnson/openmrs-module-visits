package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.util.ConfigConstants;

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
        result.setStartDatetime(dto.getStartDate());
        result.setLocation(locationService.getLocationByUuid(dto.getLocation()));
        result.setVisitType(visitService.getVisitTypeByUuid(dto.getType()));
        setVisitStatus(result, dto.getStatus());
        setVisitTime(result, dto.getTime());
        return result;
    }

    public void setVisitStatus(Visit visit, String status) {
        setAttribute(visit, ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID, status);
    }

    private void setVisitTime(Visit visit, String time) {
        setAttribute(visit, ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID, time);
    }

    private void setAttribute(Visit visit, String typeUuid, String value) {
        VisitAttribute attribute = getAttributeOfType(visit, typeUuid);
        if (StringUtils.isBlank(value)) {
            voidAttributeIfExist(visit, attribute);
        } else {
            setAttribute(visit, attribute, typeUuid, value);
        }
    }

    private void setAttribute(Visit visit, VisitAttribute attribute, String typeUuid, String value) {
        if (attribute == null) {
            visit.addAttribute(getVisitAttribute(typeUuid, value));
        } else {
            attribute.setValueReferenceInternal(value);
            visit.setAttribute(attribute);
        }
    }

    private void voidAttributeIfExist(Visit visit, VisitAttribute attribute) {
        if (attribute != null) {
            attribute.setVoided(true);
            visit.setAttribute(attribute);
        }
    }

    private VisitAttribute getAttributeOfType(Visit visit, String typeUuid) {
        for (VisitAttribute attribute : visit.getActiveAttributes()) {
            if (attribute.getAttributeType().getUuid().equals(typeUuid)) {
                return attribute;
            }
        }
        return null;
    }

    private VisitAttribute getVisitAttribute(String typeUuid, String value) {
        VisitAttribute result = new VisitAttribute();
        result.setAttributeType(visitService.getVisitAttributeTypeByUuid(typeUuid));
        result.setValue(value);
        return result;
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
