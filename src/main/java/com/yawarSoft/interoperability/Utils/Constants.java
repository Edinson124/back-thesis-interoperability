package com.yawarSoft.interoperability.Utils;

import java.time.Duration;

public class Constants {
    private Constants() {
    }

    // Códigos estándar para procedimientos
    public static final String DONATION_CODE = "1788001";      // Donación
    public static final String TRANSFUSION_CODE = "5447007";   // Transfusión

    // System base para identifiers
    public static final String BASE_SYSTEM = "http://yawarsoft.com/fhir/sid/";

    public static final String BASE_SYSTEM_CODE = "http://yawarsoft.com/fhir/codesystem/";

    public static Long getTimeToken(){
        Duration expiration = Duration.ofHours(4);
        return System.currentTimeMillis() + expiration.toMillis();
    }

}
