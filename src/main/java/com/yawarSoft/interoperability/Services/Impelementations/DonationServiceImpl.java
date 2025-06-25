package com.yawarSoft.interoperability.Services.Impelementations;

import com.yawarSoft.interoperability.Entities.BloodBankEntity;
import com.yawarSoft.interoperability.Entities.DonationEntity;
import com.yawarSoft.interoperability.Entities.DonorEntity;
import com.yawarSoft.interoperability.Enums.DonationStatus;
import com.yawarSoft.interoperability.Repositories.DonationRepository;
import com.yawarSoft.interoperability.Repositories.DonorRepository;
import com.yawarSoft.interoperability.Services.Interfaces.DonationService;
import com.yawarSoft.interoperability.Utils.AESGCMEncryptionUtil;
import com.yawarSoft.interoperability.Utils.HmacUtil;
import org.hl7.fhir.r4.model.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.yawarSoft.interoperability.Utils.Constants.BASE_SYSTEM;
import static com.yawarSoft.interoperability.Utils.Constants.DONATION_CODE;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final HmacUtil hmacUtil;
    private final AESGCMEncryptionUtil aesGCMEncryptionUtil;

    public DonationServiceImpl(DonationRepository donationRepository, DonorRepository donorRepository, HmacUtil hmacUtil, AESGCMEncryptionUtil aesGCMEncryptionUtil) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.hmacUtil = hmacUtil;
        this.aesGCMEncryptionUtil = aesGCMEncryptionUtil;
    }

    @Override
    public List<Procedure> findDonationsByDonor(String documentType, String value) {
        String docInfoDonor = documentType + '|' + value;
        String searchHash = hmacUtil.generateHmac(docInfoDonor);
        DonorEntity existingDonor = donorRepository.findBySearchHash(searchHash)
                .orElseThrow(() -> new IllegalArgumentException("Donante no encontrado con documento: "
                        + documentType + " - "+value));

        Long donorId = existingDonor.getId();
        List<DonationEntity> donations = donationRepository.findByDonorId(donorId);

        // Ahora mapeamos todas las donaciones
        return donations.stream()
                .map(donation -> toFhirProcedureForDonation(donation, existingDonor, documentType, value))
                .toList();
    }

    private Procedure toFhirProcedureForDonation(DonationEntity entity, DonorEntity donorEntity,
                                                 String documentType, String value) {
        Procedure procedure = new Procedure();
        procedure.setId(entity.getId().toString());

        DonationStatus statusEnum = DonationStatus.fromLabel(entity.getStatus());
        procedure.setStatus(statusEnum.toFhirStatus());

        // Subject
        procedure.setSubject(new Reference()
                .setIdentifier(new Identifier()
                        .setSystem(BASE_SYSTEM + documentType)
                        .setValue(value)));

        // Fecha
        procedure.setPerformed(new DateTimeType(entity.getDate().toString()));
        // Code (Donación)
        procedure.setCode(new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(DONATION_CODE)
                        .setDisplay("Blood unit collection for directed donation, donor")));

        BloodBankEntity bank = entity.getBloodBank();
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent()
                .setActor(new Reference("Organization/" + bank.getId())
                        .setDisplay(bank.getName())));

        if (statusEnum == DonationStatus.FINISHED_TEMP_DEFER || statusEnum == DonationStatus.FINISHED_PERM_DEFER) {
            procedure.addNote(new Annotation()
                    .setText(entity.getStatus()));
        }

        return procedure;
    }
}
