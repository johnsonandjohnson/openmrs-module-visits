/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.visits.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.List;

/**
 * Maps objects between related types
 *
 * @param <T> type of DTO object
 * @param <R> type of DAO object
 */
public interface Mapper<T, R extends BaseOpenmrsData> {

    /**
     * Maps a DAO object to a DTO object
     *
     * @param dao DAO object
     * @return a DTO object
     */
    T toDto(R dao);

    /**
     * Maps a DTO object to a DAO object
     *
     * @param dto DTO object
     * @return a DAO object
     */
    R fromDto(T dto);

    /**
     * Maps a list of DAO objects to a list of DTO objects
     *
     * @param daos list of DAO objects
     * @return a list of DTO objects
     */
    List<T> toDtos(List<R> daos);

    /**
     * Maps a list of DTO objects to a list of DAO objects
     *
     * @param dtos list of DTO objects
     * @return a list of DAO objects
     */
    List<R> fromDtos(List<T> dtos);
}
