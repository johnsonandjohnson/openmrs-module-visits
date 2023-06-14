package org.openmrs.module.visits.rest.web.resource;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.rest.web.VisitsRestConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Resource(order = VisitsRestConstants.VISITS_RESOURCE_ORDER, name = RestConstants.VERSION_1 + "/patient",
    supportedClass = Patient.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class PatientResolvedResource extends PatientResource1_8 {
  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    if (rep.getRepresentation().equals(VisitsRestConstants.RESOLVED_PATIENT_REPRESENTATION)) {
      final DelegatingResourceDescription description = new DelegatingResourceDescription();
      addPatientProperties(description);
      addIdentifierProperties(description);
      return description;
    }

    return super.getRepresentationDescription(rep);
  }

  private void addPatientProperties(DelegatingResourceDescription description) {
    description.addProperty("uuid");
    description.addProperty("display", this.findMethod("getDisplayString"));
    description.addProperty("identifiers", Representation.REF);
    description.addProperty("person", Representation.DEFAULT);
    description.addProperty("voided");
    description.addSelfLink();
    description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
  }

  private void addIdentifierProperties(DelegatingResourceDescription description) {
    getPatientIdentifierTypeMap().values().forEach(type -> addIdentifierTypeProperty(description, type));
  }

  private void addIdentifierTypeProperty(DelegatingResourceDescription description,
                                         PatientIdentifierType patientIdentifierType) {
    description.addProperty(patientIdentifierType.getName());
  }

  @Override
  public Object getProperty(Patient instance, String propertyName) throws ConversionException {
    final PatientIdentifierType patientIdentifierType = getPatientIdentifierTypeMap().get(propertyName);

    if (patientIdentifierType != null) {
      final List<String> identifierProperties = instance
          .getPatientIdentifiers(patientIdentifierType)
          .stream()
          .map(PatientIdentifier::getIdentifier)
          .collect(Collectors.toList());

      if (identifierProperties.isEmpty()) {
        return null;
      } else if (identifierProperties.size() == 1) {
        return identifierProperties.get(0);
      } else {
        return identifierProperties;
      }
    }

    return super.getProperty(instance, propertyName);
  }

  private Map<String, PatientIdentifierType> getPatientIdentifierTypeMap() {
    return Context
        .getPatientService()
        .getAllPatientIdentifierTypes()
        .stream()
        .collect(Collectors.toMap(PatientIdentifierType::getName, Function.identity(), this::mergeWithException));
  }

  private PatientIdentifierType mergeWithException(PatientIdentifierType patientIdentifierType1,
                                                   PatientIdentifierType patientIdentifierType2) {
    throw new APIException(MessageFormat.format("Duplicated name for Patient Identifier Type {0}, duplicated uuid: {1}, {2}",
        patientIdentifierType1.getName(), patientIdentifierType1.getUuid(), patientIdentifierType2.getUuid()));
  }
}
