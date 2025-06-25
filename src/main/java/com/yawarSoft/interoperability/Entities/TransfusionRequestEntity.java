package com.yawarSoft.interoperability.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transfusion_request")
public class TransfusionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "id_blood_bank", nullable = false)
    private BloodBankEntity bloodBank;

    @ManyToOne()
    @JoinColumn(name = "id_patient", nullable = false)
    private PatientEntity patient;

    @OneToOne
    @JoinColumn(name = "id_transfusion_result")
    private TransfusionResultEntity transfusionResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attending_doctor", nullable = false)
    private UserEntity attendingDoctor;

    @Column(name = "has_crossmatch", nullable = false)
    private Boolean hasCrossmatch;

    @Column(nullable = false, length = 50)
    private String bed;

    @Column(name = "medical_service", nullable = false, length = 100)
    private String medicalService;

    private LocalDate date;

    @Column( nullable = false)
    private String diagnosis;

    @Column(name = "request_reason", nullable = false, columnDefinition = "TEXT")
    private String requestReason;


    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private UserEntity updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
