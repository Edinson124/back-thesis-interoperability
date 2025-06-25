package com.yawarSoft.interoperability.Services.Interfaces;

import org.hl7.fhir.r4.model.Procedure;

import java.util.List;

public interface DonationService {
    List<Procedure> findDonationsByDonor(String documentType, String value);
}
