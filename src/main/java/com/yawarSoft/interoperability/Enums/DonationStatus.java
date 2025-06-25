package com.yawarSoft.interoperability.Enums;

import lombok.Getter;
import org.hl7.fhir.r4.model.Procedure;

@Getter
public enum DonationStatus {
    IN_PROCRESS("En proceso") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.INPROGRESS;
        }
    },
    FINISHED_TEMP_DEFER("Finalizada con diferimiento temporal") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.STOPPED;
        }
    },
    FINISHED_PERM_DEFER("Finalizada con diferimiento permanente") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.STOPPED;
        }
    },
    FINISHED("Finalizada") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.COMPLETED;
        }
    };

    private final String label;

    DonationStatus(String label) {
        this.label = label;
    }

    public abstract Procedure.ProcedureStatus toFhirStatus();

    public static DonationStatus fromLabel(String label) {
        for (DonationStatus status : DonationStatus.values()) {
            if (status.getLabel().equalsIgnoreCase(label.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de donación desconocido para label: " + label);
    }

}