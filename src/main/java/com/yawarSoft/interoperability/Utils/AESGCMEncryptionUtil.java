package com.yawarSoft.interoperability.Utils;


import com.yawarSoft.interoperability.Services.Interfaces.ParameterEncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESGCMEncryptionUtil {

    private static final String AES = "AES";
    private static final int GCM_IV_LENGTH = 12;  // Longitud recomendada del IV para AES-GCM (12 bytes)
    private static final int GCM_TAG_LENGTH = 128;  // Longitud del tag de autenticación (en bits)

    private final SecretKey secretKey;

    @Autowired
    public AESGCMEncryptionUtil(@Qualifier("dummy") ParameterEncryptService parameterEncryptService) throws Exception {
        String base64Key = parameterEncryptService.getParameterValue();  // Usa el nombre parámetro
        byte[] keyBytes = Base64.getDecoder().decode(base64Key); // Decodifica la clave en base64
        this.secretKey = new SecretKeySpec(keyBytes, AES);
    }

    public AESGCMEncryptionUtil(byte[] keyBytes) {
        this.secretKey = new SecretKeySpec(keyBytes, AES);
    }

    public String encrypt(String data) throws Exception {
        if(data == null ) return null;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Concatenar IV + Datos Cifrados
        byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

        // Retornar la cadena en Base64 para facilitar su almacenamiento en base de datos
        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public String decrypt(String encryptedData) throws Exception {
        if(encryptedData == null ) return null;
        byte[] encryptedDataWithIv = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedDataWithIv, 0, iv, 0, iv.length);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedBytes = new byte[encryptedDataWithIv.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedDataWithIv, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);

        return new String(decryptedData);
    }

    // Obtener la clave AES en forma de array de bytes (por si deseas almacenarla en base de datos)
    public byte[] getSecretKey() {
        return secretKey.getEncoded();
    }
}