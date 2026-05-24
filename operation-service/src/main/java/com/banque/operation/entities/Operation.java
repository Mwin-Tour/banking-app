package com.banque.operation.entities;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateOperation;

    @Column(precision = 19, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private TypeOperation typeOperation;

    private String description;

    private String numeroCompteSource;

    private String numeroCompteDestination;

    @PrePersist
    protected void onCreate() {
        dateOperation = LocalDateTime.now();
    }
}