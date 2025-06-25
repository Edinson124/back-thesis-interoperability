package com.yawarSoft.interoperability.Entities;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "donors")
public class DonorEntity extends PersonEntity {

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "place_of_origin")
    private String placeOfOrigin;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column
    private String trips;

    @Column(nullable = false)
    private String status;

    @Column(name = "donation_request", nullable = false)
    private boolean donationRequest;

    @Column(name = "deferral_end_date")
    private LocalDate deferralEndDate;

    @Column(name = "deferral_reason")
    private String deferralReason;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    private UserEntity updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

