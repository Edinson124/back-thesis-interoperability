package com.yawarSoft.interoperability.Repositories;


import com.yawarSoft.interoperability.Entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    boolean existsBySearchHash(String searchHash);
    Optional<PatientEntity> findBySearchHash(String searchHash);
    @Query("SELECT p.id FROM PatientEntity p WHERE p.searchHash = :searchHash")
    Optional<Long> findIdBySearchHash(@Param("searchHash") String searchHash);
}
