package com.yawarSoft.interoperability.Repositories;


import com.yawarSoft.interoperability.Entities.TransfusionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransfusionRequestRepository extends JpaRepository<TransfusionRequestEntity, Long>, JpaSpecificationExecutor<TransfusionRequestEntity> {
    List<TransfusionRequestEntity> findByPatientId(Long patientId);
}
