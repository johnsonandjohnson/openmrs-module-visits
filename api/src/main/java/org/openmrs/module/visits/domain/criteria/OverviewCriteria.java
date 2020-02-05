package org.openmrs.module.visits.domain.criteria;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.openmrs.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OverviewCriteria extends BaseCriteria implements Serializable {

    private static final long serialVersionUID = -486120008842837370L;

    private static final String PATH_SEPARATOR = ".";
    private static final String PATIENT_PATH = "patient";
    private static final String PATIENT_ALIAS = "patient";
    private static final String NAMES_PATH = PATIENT_PATH + PATH_SEPARATOR + "names";
    private static final String NAMES_ALIAS = "names";
    private static final String IDENTIFIERS_PATH = PATIENT_PATH + PATH_SEPARATOR + "identifiers";
    private static final String IDENTIFIERS_ALIAS = "identifiers";
    private static final String IDENTIFIERS_PREFERRED_PATH = "identifiers.preferred";
    private static final String IDENTIFIERS_IDENTIFIER_PATH = "identifiers.identifier";
    private static final String NAMES_PREFERRED_PATH = "names.preferred";
    private static final String NAMES_GIVEN_NAME_PATH = "names.givenName";
    private static final String NAMES_MIDDLE_NAME_PATH = "names.middleName";
    private static final String NAMES_FAMILY_NAME_PATH = "names.familyName";

    private Location location;

    private String query;

    public OverviewCriteria(Location location, String query) {
        this.location = location;
        this.query = query;
    }

    public String getLocationUuid() {
        return location.getUuid();
    }

    @Override
    public void loadHibernateCriteria(Criteria hibernateCriteria) {
        hibernateCriteria.add(Restrictions.eq("location", location));
        addQueryCriteria(hibernateCriteria);
    }

    public static OverviewCriteria forLocationUuid(String uuid) {
        Location location = new Location();
        location.setUuid(uuid);
        return new OverviewCriteria(location, null);
    }

    private void addQueryCriteria(Criteria c) {
        if (StringUtils.isNotBlank(query)) {
            c.createAlias(PATIENT_PATH, PATIENT_ALIAS, JoinType.LEFT_OUTER_JOIN);
            c.createAlias(NAMES_PATH, NAMES_ALIAS, JoinType.LEFT_OUTER_JOIN);
            c.createAlias(IDENTIFIERS_PATH, IDENTIFIERS_ALIAS, JoinType.LEFT_OUTER_JOIN);
            c.add(Restrictions.or(getIdentifierCriterion(), getNameCriteria()));
        }
    }

    private Criterion getNameCriteria() {
        QuerySplitter splitter = new QuerySplitter(query);
        String[] queries = splitter.splitQuery();
        List<Criterion> criteria = new ArrayList<>();
        for (String q : queries) {
            criteria.add(getNameCriterion(q));
        }
        Criterion[] array = criteria.toArray(new Criterion[0]);
        return Restrictions.and(array);
    }

    private Criterion getNameCriterion(String query) {
        return Restrictions.and(
                Restrictions.eq(NAMES_PREFERRED_PATH, true),
                Restrictions.or(
                        Restrictions.ilike(NAMES_GIVEN_NAME_PATH, query, MatchMode.ANYWHERE),
                        Restrictions.ilike(NAMES_MIDDLE_NAME_PATH, query, MatchMode.ANYWHERE),
                        Restrictions.ilike(NAMES_FAMILY_NAME_PATH, query, MatchMode.ANYWHERE)));
    }

    private Criterion getIdentifierCriterion() {
        return Restrictions.and(
                Restrictions.eq(IDENTIFIERS_PREFERRED_PATH, true),
                Restrictions.ilike(IDENTIFIERS_IDENTIFIER_PATH, query, MatchMode.ANYWHERE));
    }
}
