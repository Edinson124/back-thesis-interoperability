package com.yawarSoft.interoperability.Utils;


import com.yawarSoft.interoperability.Services.Interfaces.ParameterHashService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class HmacUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final SecretKeySpec secretKeySpec;

    public HmacUtil(@Qualifier("dummy") ParameterHashService parameterHashService) {
        String secretKey = parameterHashService.getParameterValue();
        this.secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGORITHM);
    }

    public String generateHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}
