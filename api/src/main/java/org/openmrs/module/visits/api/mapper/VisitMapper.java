package org.openmrs.module.visits.api.mapper;

import org.apache.commons.lang.NotImplementedException;
import org.openmrs.Visit;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.dto.VisitDTO;

public final class VisitMapper extends AbstractMapper<VisitDTO, Visit> {

    @Override
    public VisitDTO toDto(Visit visit) {
        VisitDecorator visitDecorator = new VisitDecorator(visit);
        return new VisitDTO(
            visit.getUuid(),
            visit.getStartDatetime(),
            visitDecorator.getTime(),
            visit.getLocation() == null ? null : visit.getLocation().getName(),
            visit.getVisitType() == null ? null : visit.getVisitType().getName(),
            visitDecorator.getStatus()
        );
    }

    @Override
    public Visit fromDto(VisitDTO dto) {
        throw new NotImplementedException("Mapping from dto not supported");
    }
}
