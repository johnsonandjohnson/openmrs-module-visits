package org.openmrs.module.visits.api.dto;

import java.io.Serializable;

public class NameUrlDTO implements Serializable {

    private static final long serialVersionUID = 5388170127582018965L;
    private String name;
    private String url;

    public NameUrlDTO() {
    }

    public NameUrlDTO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
