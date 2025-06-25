package com.yawarSoft.interoperability.Services.Impelementations;


import com.yawarSoft.interoperability.Services.Interfaces.ParameterHashService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Qualifier("dummy")
public class DummyParameterHashService implements ParameterHashService {

    @Value("${hmac.secret.key.dummy}")
    private String dummyKey;

    @Override
    public String getParameterValue() {
        return dummyKey;
    }
}
