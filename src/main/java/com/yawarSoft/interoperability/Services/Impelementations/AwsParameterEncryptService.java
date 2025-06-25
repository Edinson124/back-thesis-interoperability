package com.yawarSoft.interoperability.Services.Impelementations;

import com.yawarSoft.interoperability.Services.Interfaces.ParameterEncryptService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Service
@Qualifier("aws")
public class AwsParameterEncryptService implements ParameterEncryptService {
    @Value("${aws.ssm.parameter-name}")
    private String parameterName;

    private final SsmClient ssmClient;

    public AwsParameterEncryptService(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    public String getParameterValue() {
        // Usar el nombre de parámetro inyectado
        GetParameterRequest request = GetParameterRequest.builder()
                .name(parameterName)  // Se obtiene de la propiedad
                .withDecryption(true)  // Si el parámetro está cifrado
                .build();

        GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }
}
