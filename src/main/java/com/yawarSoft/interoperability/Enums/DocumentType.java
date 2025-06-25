package com.yawarSoft.interoperability.Enums;

import lombok.Getter;

import java.util.Arrays;

public enum DocumentType {
    DNI("dni", "DNI"),
    PASAPORTE("pasaporte", "PASAPORTE"),
    CE("ce", "CE"),
    UNKNOWN("", "UNKNOWN");

    private final String keyword;
    @Getter
    private final String code;

    DocumentType(String keyword, String code) {
        this.keyword = keyword;
        this.code = code;
    }

    public static DocumentType fromSystem(String system) {
        if (system == null) {
            return UNKNOWN;
        }
        return Arrays.stream(values())
                .filter(dt -> !dt.equals(UNKNOWN) && system.contains("/" + dt.keyword))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

