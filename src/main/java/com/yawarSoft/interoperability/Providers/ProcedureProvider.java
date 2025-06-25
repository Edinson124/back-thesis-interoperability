package com.yawarSoft.interoperability.Providers;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.yawarSoft.interoperability.Enums.DocumentType;
import com.yawarSoft.interoperability.Services.Interfaces.DonationService;
import com.yawarSoft.interoperability.Services.Interfaces.TransfusionService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.yawarSoft.interoperability.Utils.Constants.DONATION_CODE;
import static com.yawarSoft.interoperability.Utils.Constants.TRANSFUSION_CODE;

@Component
public class ProcedureProvider implements IResourceProvider {

    private final DonationService donationService;
    private final TransfusionService transfusionService;

    public ProcedureProvider(DonationService donationService, TransfusionService transfusionService) {
        this.donationService = donationService;
        this.transfusionService = transfusionService;
    }

    @Override
    public Class<Procedure> getResourceType() {
        return Procedure.class;
    }

    @Search
    public Bundle searchProcedures(
            @RequiredParam(name = "subject.identifier") TokenParam identifier,
            @RequiredParam(name = "code") TokenParam code)
    {
        // Leer los datos del identifier
        String system = identifier.getSystem();
        String value = identifier.getValue();

        if (system == null || value == null) {
            throw new IllegalArgumentException("Se requiere subject.identifier con system y value");
        }

        String documentType = DocumentType.fromSystem(system).getCode();

        List<Procedure> procedures = List.of();

        if (DONATION_CODE.equals(code.getValue())) {
            // Llamamos al servicio para obtener donaciones
            procedures = donationService.findDonationsByDonor(documentType, value);
        } else if (TRANSFUSION_CODE.equals(code.getValue())) {
            // Llamamos al servicio para obtener transfusiones
            procedures = transfusionService.findTransfusionsByPatient(documentType, value);
        } else {
            procedures = List.of();
        }

        // Crear el Bundle de retorno
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        procedures.forEach(proc -> bundle.addEntry().setResource(proc));

        return bundle;
    }
}
