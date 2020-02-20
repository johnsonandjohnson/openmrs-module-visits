package org.openmrs.module.visits.api.dto;

import java.io.Serializable;
import java.util.Date;

public class VisitDTO implements Serializable {

    private static final long serialVersionUID = -6043667045678304408L;

    private String uuid;
    private Date startDate;
    private String time;
    private String location;
    private String type;
    private String status;
    private String formUri;

    public VisitDTO() {
    }

    public String getUuid() {
        return uuid;
    }

    public VisitDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public VisitDTO setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getTime() {
        return time;
    }

    public VisitDTO setTime(String time) {
        this.time = time;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public VisitDTO setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getType() {
        return type;
    }

    public VisitDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public VisitDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getFormUri() {
        return formUri;
    }

    public VisitDTO setFormUri(String formUri) {
        this.formUri = formUri;
        return this;
    }
}
