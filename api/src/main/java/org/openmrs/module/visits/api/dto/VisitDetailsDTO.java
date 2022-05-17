package org.openmrs.module.visits.api.dto;

/** Object representing a visit DTO extended with the additional properties */
public class VisitDetailsDTO extends VisitDTO {

  private static final long serialVersionUID = 15L;

  private String locationName;
  private String typeName;

  public VisitDetailsDTO() {}

  /**
   * Constructor of the visit details DTO
   *
   * @param visitDTO base visit DTO object
   * @param locationName name of the location
   * @param typeName name of the type
   */
  public VisitDetailsDTO(VisitDTO visitDTO, String locationName, String typeName) {
    super(
        visitDTO.getUuid(),
        visitDTO.getLocation(),
        visitDTO.getType(),
        visitDTO.getStatus(),
        visitDTO.getFormUri(),
        visitDTO.getPatientUuid(),
        visitDTO.getVisitDateDTO());
    this.locationName = locationName;
    this.typeName = typeName;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
}
