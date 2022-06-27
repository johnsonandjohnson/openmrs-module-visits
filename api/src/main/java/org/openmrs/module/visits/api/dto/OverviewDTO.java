/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Object representing a visit overview DTO, used to display data
 */
public class OverviewDTO implements Serializable {

    private static final long serialVersionUID = 7L;

    private String uuid;
    private String patientIdentifier;
    private NameUrlDTO nameUrl;
    private Date startDate;
    private String time;
    private String type;
    private String status;
    private String location;
    private Date actualDate;

    public String getUuid() {
        return uuid;
    }

    public OverviewDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public OverviewDTO setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
        return this;
    }

    public NameUrlDTO getNameUrl() {
        return nameUrl;
    }

    public OverviewDTO setNameUrl(NameUrlDTO nameUrl) {
        this.nameUrl = nameUrl;
        return this;
    }

    public Date getStartDate() {
        if (null != startDate) {
            return new Date(startDate.getTime());
        } else {
            return null;
        }
    }

    public OverviewDTO setStartDate(Date startDate) {
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
        return this;
    }

    public String getTime() {
        return time;
    }

    public OverviewDTO setTime(String time) {
        this.time = time;
        return this;
    }

    public String getType() {
        return type;
    }

    public OverviewDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OverviewDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public OverviewDTO setLocation(String location) {
        this.location = location;
        return this;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public OverviewDTO setActualDate(Date actualDate) {
        this.actualDate = actualDate != null ? new Date(actualDate.getTime()) : null;
        return this;
    }
}
