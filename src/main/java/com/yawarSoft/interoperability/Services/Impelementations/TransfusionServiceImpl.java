package com.yawarSoft.interoperability.Services.Impelementations;

import com.yawarSoft.interoperability.Entities.*;
import com.yawarSoft.interoperability.Enums.DonationStatus;
import com.yawarSoft.interoperability.Enums.TransfusionStatus;
import com.yawarSoft.interoperability.Repositories.PatientRepository;
import com.yawarSoft.interoperability.Repositories.TransfusionRequestRepository;
import com.yawarSoft.interoperability.Services.Interfaces.TransfusionService;
import com.yawarSoft.interoperability.Utils.AESGCMEncryptionUtil;
import com.yawarSoft.interoperability.Utils.HmacUtil;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yawarSoft.interoperability.Utils.Constants.*;

@Service
public class TransfusionServiceImpl implements TransfusionService {


    private final TransfusionRequestRepository transfusionRequestRepository;
    private final PatientRepository patientRepository;
    private final HmacUtil hmacUtil;
    private final AESGCMEncryptionUtil aesGCMEncryptionUtil;

    public TransfusionServiceImpl(TransfusionRequestRepository transfusionRequestRepository, PatientRepository patientRepository, HmacUtil hmacUtil, AESGCMEncryptionUtil aesGCMEncryptionUtil) {
        this.transfusionRequestRepository = transfusionRequestRepository;
        this.patientRepository = patientRepository;
        this.hmacUtil = hmacUtil;
        this.aesGCMEncryptionUtil = aesGCMEncryptionUtil;
    }


    @Override
    public List<Procedure> findTransfusionsByPatient(String documentType, String value) {
        String docInfoDonor = documentType + '|' + value;
        String searchHash = hmacUtil.generateHmac(docInfoDonor);
        PatientEntity existingPatient = patientRepository.findBySearchHash(searchHash)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con documento: "
                        + documentType + " - "+value));

        Long patientId = existingPatient.getId();
        List<TransfusionRequestEntity> transfusions = transfusionRequestRepository.findByPatientId(patientId);


        return transfusions.stream()
                .map(donation -> toFhirProcedureForDonation(donation, existingPatient, documentType, value))
                .toList();
    }

    private Procedure toFhirProcedureForDonation(TransfusionRequestEntity entity, PatientEntity donorEntity,
                                                 String documentType, String value) {
        Procedure procedure = new Procedure();
        procedure.setId(entity.getId().toString());

        TransfusionStatus statusEnum = TransfusionStatus.fromLabel(entity.getStatus());
        procedure.setStatus(statusEnum.toFhirStatus());

        // Subject
        procedure.setSubject(new Reference()
                .setIdentifier(new Identifier()
                        .setSystem(BASE_SYSTEM + documentType)
                        .setValue(value)));

        // Fecha
        procedure.setPerformed(new DateTimeType(entity.getDate().toString()));
        // Code (Donación)
        // Código estándar para transfusión
        procedure.setCode(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(TRANSFUSION_CODE)
                        .setDisplay("Transfusion")));

        BloodBankEntity bank = entity.getBloodBank();
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent()
                .setActor(new Reference("Organization/" + bank.getId())
                        .setDisplay(bank.getName())));

        if (entity.getTransfusionResult() != null) {
            TransfusionResultEntity result = entity.getTransfusionResult();
            String noteText = result.getHasReaction()
                    ? "Presentó reacciones adversas: " + (result.getReactionAdverse() != null ? result.getReactionAdverse() : "")
                    : "No presentó reacciones adversas.";
            procedure.addNote(new Annotation().setText(noteText));
        }

        return procedure;
    }
}
