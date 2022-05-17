/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PagingInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private int page;
  private int pageSize;
  private Long totalRecordCount;
  private boolean loadRecordCount;

  public PagingInfo() {}

  /**
   * Creates a new {@link PagingInfo} instance.
   *
   * @param page The 1-based number of the page being requested.
   * @param pageSize The number of records to include on each page.
   */
  public PagingInfo(int page, int pageSize) {
    this.page = page;
    this.pageSize = pageSize;
    this.loadRecordCount = true;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public Long getTotalRecordCount() {
    return totalRecordCount;
  }

  public void setTotalRecordCount(Long totalRecordCount) {
    this.totalRecordCount = totalRecordCount;

    // If the total records is set to anything other than null, than don't reload the count
    this.loadRecordCount = totalRecordCount == null;
  }

  public boolean shouldLoadRecordCount() {
    return loadRecordCount;
  }

  public void setLoadRecordCount(boolean loadRecordCount) {
    this.loadRecordCount = loadRecordCount;
  }

  public boolean hasMoreResults() {
    if (totalRecordCount == null) {
      return false;
    }
    return ((long) page * pageSize) < totalRecordCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
