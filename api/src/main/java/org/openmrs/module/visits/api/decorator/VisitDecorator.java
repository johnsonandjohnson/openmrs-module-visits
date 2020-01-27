package org.openmrs.module.visits.api.decorator;

import org.openmrs.Visit;
import org.openmrs.VisitAttribute;

import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_TIME_ATTRIBUTE_TYPE_UUID;

public class VisitDecorator extends ObjectDecorator<Visit> {

    public VisitDecorator(Visit object) {
        super(object);
    }

    public String getTime() {
        String time = null;
        VisitAttribute attribute = getAttribute(VISIT_TIME_ATTRIBUTE_TYPE_UUID);
        if (attribute != null) {
            time = String.valueOf(attribute.getValue());
        }
        return time;
    }

    public String getStatus() {
        String status = null;
        VisitAttribute attribute = getAttribute(VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
        if (attribute != null) {
            status = String.valueOf(attribute.getValue());
        }
        return status;
    }

    public VisitAttribute getAttribute(String uuid) {
        Set<VisitAttribute> attributes = new HashSet<>(getObject().getActiveAttributes());
        for (VisitAttribute attribute : attributes) {
            if (uuid.equals(attribute.getAttributeType().getUuid())) {
                return attribute;
            }
        }
        return null;
    }
}
