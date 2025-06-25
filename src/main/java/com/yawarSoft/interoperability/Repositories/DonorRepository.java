package com.yawarSoft.interoperability.Repositories;


import com.yawarSoft.interoperability.Entities.DonorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<DonorEntity, Long> {
    boolean existsBySearchHash(String searchHash);
    Optional<DonorEntity> findBySearchHash(String searchHash);

    @Query("SELECT d.id FROM DonorEntity d WHERE d.searchHash = :searchHash")
    Optional<Long> findIdBySearchHash(@Param("searchHash") String searchHash);
}