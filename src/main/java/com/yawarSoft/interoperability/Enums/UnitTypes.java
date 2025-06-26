package com.yawarSoft.interoperability.Enums;

import lombok.Getter;

@Getter
public enum UnitTypes {
    SANGRE_TOTAL("Sangre total", "12289007", null),
    CONCENTRADO_ERITROCITOS("Concentrado de eritrocitos", "126242007", null),
    PLASMA_FRESCO_CONGELADO("Plasma fresco congelado", "346447007", null),
    CRIOPRECIPITADOS("Crioprecipitados", "256395009", null),
    PLAQUETAS("Plaquetas", "256395009", null),
    AFERESIS_PLAQUETAS("Aféresis de plaquetas", "126259002", null),
    AFERESIS_GLOBULOS_ROJOS("Aféresis de glóbulos rojos", "126242007", "360165004"),
    AFERESIS_PLASMA("Aféresis de plasma", "346447007", "360165004");

    private final String label;
    private final String snomedCode;
    private final String apheresisMethodCode;

    UnitTypes(String label, String snomedCode, String apheresisMethodCode) {
        this.label = label;
        this.snomedCode = snomedCode;
        this.apheresisMethodCode = apheresisMethodCode;
    }

    public static UnitTypes fromLabel(String label) {
        for (UnitTypes unit : UnitTypes.values()) {
            if (unit.getLabel().equalsIgnoreCase(label)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("No existe UnitTypes para: " + label);
    }
}

