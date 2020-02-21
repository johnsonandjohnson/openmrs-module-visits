package org.openmrs.module.visits.util;

import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.util.Comparator;

public final class ComparatorsHelper {

    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateAsc() {
        return new Comparator<VisitDomainWrapper>() {
            @Override
            public int compare(VisitDomainWrapper v1, VisitDomainWrapper v2) {
                return v1.getStartDate().compareTo(v2.getStartDate());
            }
        };
    }

    public static Comparator<VisitDomainWrapper> getVisitsComparatorByStartDateDesc() {
        return new Comparator<VisitDomainWrapper>() {
            @Override
            public int compare(VisitDomainWrapper v1, VisitDomainWrapper v2) {
                return (-1) * v1.getStartDate().compareTo(v2.getStartDate());
            }
        };
    }

    private ComparatorsHelper() { }
}
