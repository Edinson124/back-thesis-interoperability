package com.yawarSoft.interoperability.Services.Interfaces;

import org.hl7.fhir.r4.model.Procedure;

import java.util.List;

public interface TransfusionService {
    List<Procedure> findTransfusionsByPatient(String documentType, String value);
}
