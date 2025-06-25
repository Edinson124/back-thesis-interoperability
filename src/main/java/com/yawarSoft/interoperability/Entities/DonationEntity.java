package com.yawarSoft.interoperability.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "donations")
public class DonationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "id_blood_bank", nullable = false)
    private BloodBankEntity bloodBank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_donor", nullable = false)
    private DonorEntity donor;

    @Column(name = "donation_purpose", nullable = false)
    private String donationPurpose;

    @Column(name = "blood_component", nullable = false)
    private String bloodComponent;

    @Column
    private String observation;

    @Column(nullable = false)
    private Boolean interrupted;

    @Column(name = "interruption_phase")
    private String interruptionPhase;

    @Column(name = "deferral_type")
    private String deferralType;

    @Column(name = "deferral_reason")
    private String deferralReason;

    @Column(name = "deferral_duration")
    private Integer deferralDuration;

    private LocalDate date;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

