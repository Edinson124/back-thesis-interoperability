package com.yawarSoft.interoperability.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persons")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private byte[] firstName;

    @Column(name = "last_name", nullable = false)
    private byte[] lastName;

    @Column(name = "second_last_name", nullable = false)
    private byte[] secondLastName;

    @Column(name = "document_type", nullable = false)
    private byte[] documentType;

    @Column(name = "document_number", nullable = false, unique = true)
    private byte[] documentNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private byte[] address;

    @Column(unique = true)
    private byte[] phone;

    @Column(unique = true)
    private byte[] email;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "rh_factor")
    private String rhFactor;

    @Column
    private String occupation;

    @Column(name = "search_hash", nullable = false, unique = true)
    private String searchHash;
}

