package org.openmrs.module.visits.api.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO object containing fields related to visit date and time. Created mainly for splitting big
 * constructor in {@link VisitDTO} to avoid SonarQube issues.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDateDTO implements Serializable {

  private static final long serialVersionUID = 14L;

  private Date startDate;

  private String time;

  private Date actualDate;

  public VisitDateDTO() {}

  public VisitDateDTO(Date startDate, String time, Date actualDate) {
    this.startDate = startDate;
    this.time = time;
    this.actualDate = actualDate;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Date getActualDate() {
    return actualDate;
  }

  public void setActualDate(Date actualDate) {
    this.actualDate = actualDate;
  }
}
