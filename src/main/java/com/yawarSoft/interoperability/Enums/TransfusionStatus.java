package com.yawarSoft.interoperability.Enums;

import org.hl7.fhir.r4.model.Procedure;

public enum TransfusionStatus {
    PENDIENTE("Pendiente") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.PREPARATION;
        }
    },
    ACEPTADA("Aceptada") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.PREPARATION;
        }
    },
    LIBERADA("Liberada") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.INPROGRESS;
        }
    },
    FINALIZADA("Finalizada") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.COMPLETED;
        }
    },
    NO_ATENDIDA("No atendida") {
        @Override
        public Procedure.ProcedureStatus toFhirStatus() {
            return Procedure.ProcedureStatus.STOPPED;
        }
    };

    private final String label;

    TransfusionStatus(String label) {
        this.label = label;
    }

    public abstract Procedure.ProcedureStatus toFhirStatus();

    // Para obtener por label
    public static TransfusionStatus fromLabel(String label) {
        for (TransfusionStatus status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Etiqueta desconocida para TransfusionStatus: " + label);
    }
}
