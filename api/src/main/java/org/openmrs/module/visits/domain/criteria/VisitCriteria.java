package org.openmrs.module.visits.domain.criteria;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;

import java.io.Serializable;

public class VisitCriteria extends BaseCriteria implements Serializable {

    private static final long serialVersionUID = -486120008842837370L;

    private Patient patient;

    public VisitCriteria(Patient patient) {
        this.patient = patient;
    }

    public String getPatientUuid() {
        return patient.getUuid();
    }

    @Override
    public void loadHibernateCriteria(Criteria hibernateCriteria) {
        hibernateCriteria.add(Restrictions.eq("patient", patient));
    }

    public static VisitCriteria forPatientUuid(String uuid) {
        Patient patient = new Patient();
        patient.setUuid(uuid);
        return new VisitCriteria(patient);
    }
}
