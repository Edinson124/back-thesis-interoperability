package com.yawarSoft.interoperability.Services.Interfaces;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;

import java.util.List;

public interface UnitService {
    Bundle getStockByBloodBank(String bancoId);
}

