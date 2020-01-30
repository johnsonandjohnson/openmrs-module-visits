package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;

import java.io.Serializable;

public class OverviewCriteria extends BaseCriteria implements Serializable {

    private static final long serialVersionUID = -486120008842837370L;

    private Location location;

    public OverviewCriteria(Location location) {
        this.location = location;
    }

    public String getLocationUuid() {
        return location.getUuid();
    }

    @Override
    public void loadHibernateCriteria(Criteria hibernateCriteria) {
        hibernateCriteria.add(Restrictions.eq("location", location));
    }

    public static OverviewCriteria forLocationUuid(String uuid) {
        Location location = new Location();
        location.setUuid(uuid);
        return new OverviewCriteria(location);
    }
}
