package com.bank.asset_management.entity;

import com.bank.asset_management.util.constant.AssetStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String assetCode;

    String name;

    @ManyToOne
    @JoinColumn(name = "type_id")
    AssetType type;

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User manager;

    @Enumerated(EnumType.STRING)
    AssetStatus status;

    LocalDate purchaseDate;
    Double value;
    String description;

    Instant createdAt;
    Instant updatedAt;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
    @Default
    @JsonIgnore
    List<AssetAssignment> assignments = new ArrayList<>();

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
