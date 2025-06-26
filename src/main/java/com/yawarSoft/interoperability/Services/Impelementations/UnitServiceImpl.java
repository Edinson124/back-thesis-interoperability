package com.yawarSoft.interoperability.Services.Impelementations;

import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.yawarSoft.interoperability.Dtos.StockResumenDTO;
import com.yawarSoft.interoperability.Entities.AuthExternalSystemEntity;
import com.yawarSoft.interoperability.Entities.BloodBankEntity;
import com.yawarSoft.interoperability.Enums.BloodSNOMED;
import com.yawarSoft.interoperability.Enums.NetworkBBStatus;
import com.yawarSoft.interoperability.Enums.UnitTypes;
import com.yawarSoft.interoperability.Repositories.BloodBankNetworkRepository;
import com.yawarSoft.interoperability.Repositories.BloodBankRepository;
import com.yawarSoft.interoperability.Repositories.UnitRepository;
import com.yawarSoft.interoperability.Services.Interfaces.AuthenticatedExternalClientService;
import com.yawarSoft.interoperability.Services.Interfaces.UnitService;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.yawarSoft.interoperability.Utils.Constants.BASE_SYSTEM_CODE;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final BloodBankRepository bloodBankRepository;
    private final AuthenticatedExternalClientService authenticatedExternalClientService;
    private final BloodBankNetworkRepository bloodBankNetworkRepository;

    public UnitServiceImpl(UnitRepository unitRepository, BloodBankRepository bloodBankRepository, AuthenticatedExternalClientService authenticatedExternalClientService, BloodBankNetworkRepository bloodBankNetworkRepository) {
        this.unitRepository = unitRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.authenticatedExternalClientService = authenticatedExternalClientService;
        this.bloodBankNetworkRepository = bloodBankNetworkRepository;
    }


    @Override
    public Bundle getStockByBloodBank(String bloodBankId) {
        AuthExternalSystemEntity authExternalSystem = authenticatedExternalClientService.getExternalClient();
        Integer idBloodBankAuth = authExternalSystem.getBloodBank().getId();

        Integer id = Integer.valueOf(bloodBankId);

        BloodBankEntity banco = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco de sangre no encontrado"));
        String nombreBanco = banco.getName();

        long relations = bloodBankNetworkRepository.countActiveRelationsBetweenBanks(
                idBloodBankAuth, id, NetworkBBStatus.ACTIVE.name());

        if (relations == 0) {
            throw new ForbiddenOperationException("No pertenece a una misma red.");
        }


        List<StockResumenDTO> resumenDB = unitRepository.getResumenStockByBloodBank(id);
        List<Observation> observations = buildObservations(resumenDB, id, nombreBanco);
        // Ahora crear el Bundle
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        bundle.setId(UUID.randomUUID().toString());
        bundle.setTimestamp(new Date());

        for (Observation obs : observations) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(obs);
            bundle.addEntry(entry);
        }

        return bundle;
    }

    private List<Observation> buildObservations(List<StockResumenDTO> resumenDB, Integer id, String nombreBanco) {
        List<String> bloodTypes = List.of("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
        List<String> unitTypes = Arrays.stream(UnitTypes.values())
                .map(UnitTypes::getLabel)
                .toList();

        Map<String, StockResumenDTO> resumenMap = new HashMap<>();
        for (StockResumenDTO dto : resumenDB) {
            String key = dto.getBloodType() + "|" + dto.getUnitType();
            resumenMap.put(key, dto);
        }

        List<Observation> observations = new ArrayList<>();

        for (String blood : bloodTypes) {
            for (String unit : unitTypes) {
                String key = blood + "|" + unit;

                Long quantity = resumenMap.containsKey(key) ? resumenMap.get(key).getQuantity() : 0L;

                Observation obs = new Observation();
                obs.setId(IdType.newRandomUuid());
                obs.setStatus(Observation.ObservationStatus.FINAL);

                // ➕ Concepto estándar para representar "Stock de unidades de sangre"
                obs.setCode(new CodeableConcept()
                        .addCoding(new Coding()
                                .setSystem(BASE_SYSTEM_CODE)
                                .setCode("stock-unidades-sangre")
                                .setDisplay("Stock de unidades de sangre"))
                        .setText("Stock de unidades de sangre"));

                // ➕ Quantity
                obs.setValue(new Quantity().setValue(quantity).setUnit("unidades"));

                // ➕ Grupo sanguíneo y Rh
                String bloodGroup = blood.replace("+", "").replace("-", "");
                boolean isRhPositive = blood.contains("+");
                BloodSNOMED bloodSNOMED = BloodSNOMED.fromLabel(bloodGroup);

                CodeableConcept bloodGroupConcept = new CodeableConcept()
                        .addCoding(new Coding()
                                .setSystem("http://snomed.info/sct")
                                .setCode(bloodSNOMED.getSnomedCode())
                                .setDisplay(bloodSNOMED.getDisplay()))
                        .setText("Grupo sanguíneo " + bloodGroup);

                CodeableConcept rhConcept = new CodeableConcept()
                        .addCoding(new Coding()
                                .setSystem("http://snomed.info/sct")
                                .setCode(isRhPositive ? "165747007" : "165746003")
                                .setDisplay(isRhPositive ? "Rh positivo" : "Rh negativo"))
                        .setText("Factor Rh");

                // ➕ Tipo de unidad
                UnitTypes unitTypeEnum = UnitTypes.fromLabel(unit);
                CodeableConcept unitTypeConcept = new CodeableConcept()
                        .addCoding(new Coding()
                                .setSystem("http://snomed.info/sct")
                                .setCode(unitTypeEnum.getSnomedCode())
                                .setDisplay(unitTypeEnum.getLabel()))
                        .setText(unitTypeEnum.getLabel());

                obs.addComponent(new Observation.ObservationComponentComponent()
                        .setCode(new CodeableConcept().setText("Grupo sanguíneo"))
                        .setValue(bloodGroupConcept));

                obs.addComponent(new Observation.ObservationComponentComponent()
                        .setCode(new CodeableConcept().setText("Factor Rh"))
                        .setValue(rhConcept));

                obs.addComponent(new Observation.ObservationComponentComponent()
                        .setCode(new CodeableConcept().setText("Tipo de unidad"))
                        .setValue(unitTypeConcept));

                // ➕ Si aplica método de Aféresis
                if (unitTypeEnum.getApheresisMethodCode() != null) {
                    obs.setMethod(new CodeableConcept()
                            .addCoding(new Coding()
                                    .setSystem("http://snomed.info/sct")
                                    .setCode(unitTypeEnum.getApheresisMethodCode())
                                    .setDisplay("Apheresis - action"))
                            .setText("Apheresis"));
                }

                // ➕ Asignamos Organization
                obs.addPerformer(new Reference("Organization/" + id).setDisplay(nombreBanco));

                observations.add(obs);
            }
        }

        return observations;
    }


}
