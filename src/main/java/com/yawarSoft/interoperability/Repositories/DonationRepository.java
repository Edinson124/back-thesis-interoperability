package com.yawarSoft.interoperability.Repositories;


import com.yawarSoft.interoperability.Entities.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, Long> {
    List<DonationEntity> findByDonorId(Long donorId);
}
