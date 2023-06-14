package org.openmrs.module.visits.api.service;

import org.openmrs.module.visits.domain.PagingInfo;

public class VisitSimpleQuery {
  public static final String SORT_ASCENDING = "asc";
  public static final String SORT_DESCENDING = "desc";

  private final String locationUuid;
  private final String patientUuid;
  private final PagingInfo pagingInfo;
  private final String query;
  private final String visitStatus;
  private final Long dateFrom;
  private final Long dateTo;
  private final String timePeriod;
  private final String startDatetimeSort;

  private VisitSimpleQuery(String locationUuid, String patientUuid, PagingInfo pagingInfo, String query, String visitStatus,
                           Long dateFrom, Long dateTo, String timePeriod, String startDatetimeSort) {
    this.locationUuid = locationUuid;
    this.patientUuid = patientUuid;
    this.pagingInfo = pagingInfo;
    this.query = query;
    this.visitStatus = visitStatus;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.timePeriod = timePeriod;
    this.startDatetimeSort = startDatetimeSort;
  }

  public String getLocationUuid() {
    return locationUuid;
  }

  public String getPatientUuid() {
    return patientUuid;
  }

  public PagingInfo getPagingInfo() {
    return pagingInfo;
  }

  public String getQuery() {
    return query;
  }

  public String getVisitStatus() {
    return visitStatus;
  }

  public Long getDateFrom() {
    return dateFrom;
  }

  public Long getDateTo() {
    return dateTo;
  }

  public String getTimePeriod() {
    return timePeriod;
  }

  public String getStartDatetimeSort() {
    return startDatetimeSort;
  }

  public static class Builder {
    private String locationUuid;
    private String patientUuid;
    private PagingInfo pagingInfo;
    private String query;
    private String visitStatus;
    private Long dateFrom;
    private Long dateTo;
    private String timePeriod;
    private String startDatetimeSort;

    public Builder withLocationUuid(String locationUuid) {
      this.locationUuid = locationUuid;
      return this;
    }

    public Builder withPatientUuid(String patientUuid) {
      this.patientUuid = patientUuid;
      return this;
    }

    public Builder withPagingInfo(PagingInfo pagingInfo) {
      this.pagingInfo = pagingInfo;
      return this;
    }

    public Builder withQuery(String query) {
      this.query = query;
      return this;
    }

    public Builder withVisitStatus(String visitStatus) {
      this.visitStatus = visitStatus;
      return this;
    }

    public Builder withDateFrom(Long dateFrom) {
      this.dateFrom = dateFrom;
      return this;
    }

    public Builder withDateTo(Long dateTo) {
      this.dateTo = dateTo;
      return this;
    }

    public Builder withTimePeriod(String timePeriod) {
      this.timePeriod = timePeriod;
      return this;
    }

    public Builder withDtartDatetimeSort(String startDatetimeSort) {
      this.startDatetimeSort = startDatetimeSort;
      return this;
    }

    public VisitSimpleQuery build() {
      return new VisitSimpleQuery(locationUuid, patientUuid, pagingInfo, query, visitStatus, dateFrom, dateTo, timePeriod,
          startDatetimeSort);
    }
  }
}
