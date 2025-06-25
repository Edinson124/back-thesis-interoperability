package com.yawarSoft.interoperability.Services.Impelementations;


import com.yawarSoft.interoperability.Services.Interfaces.ParameterHashService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Service
@Qualifier("aws")
public class AwsParameterHashService implements ParameterHashService {
    private final SsmClient ssmClient;

    @Value("${aws.ssm.parameter-hash}")
    private String parameterHashName;

    public AwsParameterHashService(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    public String getParameterValue() {
        GetParameterRequest request = GetParameterRequest.builder()
                .name(parameterHashName)  // Obtiene el parámetro usando el nombre inyectado
                .withDecryption(true)  // Si está cifrado
                .build();

        GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }
}
