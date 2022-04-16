package org.openmrs.module.visits.builder;

import org.openmrs.module.visits.api.dto.VisitDateDTO;

import java.util.Date;

public class VisitDateDTOBuilder extends AbstractBuilder<VisitDateDTO> {

    private Date startDate;

    private String time;

    private Date actualDate;

    @Override
    public VisitDateDTO build() {
        return buildAsNew();
    }

    @Override
    public VisitDateDTO buildAsNew() {
        VisitDateDTO visitDateDTO = new VisitDateDTO();
        visitDateDTO.setStartDate(startDate);
        visitDateDTO.setTime(time);
        visitDateDTO.setActualDate(actualDate);

        return visitDateDTO;
    }

    public VisitDateDTOBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public VisitDateDTOBuilder withTime(String time) {
        this.time = time;
        return this;
    }

    public VisitDateDTOBuilder withActualDate(Date actualDate) {
        this.actualDate = actualDate;
        return this;
    }
}
