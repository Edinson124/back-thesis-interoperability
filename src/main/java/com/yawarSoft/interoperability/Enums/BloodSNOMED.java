package com.yawarSoft.interoperability.Enums;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BloodSNOMED {
    A("A", "112144000", "Grupo sanguíneo A"),
    B("B", "112149005", "Grupo sanguíneo B"),
    AB("AB", "165743006", "Grupo sanguíneo AB"),
    O("O", "58460004", "Grupo sanguíneo O");

    private final String label;
    private final String snomedCode;
    private final String display;

    BloodSNOMED(String label, String snomedCode, String display) {
        this.label = label;
        this.snomedCode = snomedCode;
        this.display = display;
    }

    public static BloodSNOMED fromLabel(String label) {
        return Arrays.stream(values())
                .filter(bg -> bg.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Grupo sanguíneo inválido: " + label));
    }
}