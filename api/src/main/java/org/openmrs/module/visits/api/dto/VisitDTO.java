package org.openmrs.module.visits.api.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/** Object representing a visit DTO */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDTO implements Serializable {

  private static final long serialVersionUID = 3L;

  private String uuid;
  private Date startDate;
  private String time;
  private String location;
  private String type;
  private String status;
  private String formUri;
  private Date actualDate;
  private String patientUuid;
  private VisitDateDTO visitDateDTO;

  public VisitDTO() {}

  /**
   * Constructor of a Visit DTO object
   *
   * @param uuid uuid of the visit
   * @param location location of the visit
   * @param type type of the visit (eg. follow-up)
   * @param status status of the visit (eg. SCHEDULED)
   * @param formUri visit form URI
   * @param patientUuid uuid of the patient
   */
  @SuppressWarnings("checkstyle:parameternumber")
  public VisitDTO(
      String uuid,
      String location,
      String type,
      String status,
      String formUri,
      String patientUuid,
      VisitDateDTO visitDateDTO) {
    this.uuid = uuid;
    this.startDate =
        (visitDateDTO.getStartDate() != null
            ? new Date(visitDateDTO.getStartDate().getTime())
            : null);
    this.time = visitDateDTO.getTime();
    this.location = location;
    this.type = type;
    this.status = status;
    this.formUri = formUri;
    this.actualDate =
        (visitDateDTO.getActualDate() != null
            ? new Date(visitDateDTO.getActualDate().getTime())
            : null);
    this.patientUuid = patientUuid;
  }

  // copy constructor
  public VisitDTO(VisitDTO visitDTO) {
    this.uuid = visitDTO.getUuid();
    this.startDate = visitDTO.getStartDate();
    this.time = visitDTO.getTime();
    this.location = visitDTO.getLocation();
    this.type = visitDTO.getType();
    this.status = visitDTO.getStatus();
    this.formUri = visitDTO.getFormUri();
    this.actualDate = visitDTO.getActualDate();
    this.patientUuid = visitDTO.getPatientUuid();
    this.visitDateDTO = visitDTO.getVisitDateDTO();
  }

  public String getUuid() {
    return uuid;
  }

  public VisitDTO setUuid(String uuid) {
    this.uuid = uuid;
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

  public String getPatientUuid() {
    return patientUuid;
  }

  public VisitDTO setPatientUuid(String patientUuid) {
    this.patientUuid = patientUuid;
    return this;
  }

  public VisitDTO setVisitDateDTO(VisitDateDTO visitDateDTO) {
    this.visitDateDTO = visitDateDTO;
    return this;
  }

  public VisitDateDTO getVisitDateDTO() {
    return visitDateDTO;
  }

  public VisitDTO setStartDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  public Date getStartDate() {
    return startDate;
  }

  public VisitDTO setTime(String time) {
    this.time = time;
    return this;
  }

  public String getTime() {
    return time;
  }

  public VisitDTO setActualDate(Date actualDate) {
    this.actualDate = actualDate;
    return this;
  }

  public Date getActualDate() {
    return actualDate;
  }
}
