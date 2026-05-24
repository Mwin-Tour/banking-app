package com.banque.compte.entities;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_compte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCompte;

    private LocalDateTime dateCreation;

    @Column(precision = 19, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatutCompte statut;

    @Enumerated(EnumType.STRING)
    private Devise devise;

    private Long clientId;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = StatutCompte.CREE;
        }
        if (numeroCompte == null) {
            numeroCompte = "CPT" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }

    public void crediter(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.solde = this.solde.add(montant);
    }

    public void debiter(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (!peutDebiter(montant)) {
            throw new IllegalStateException("Solde insuffisant pour débiter");
        }
        this.solde = this.solde.subtract(montant);
    }

    public abstract boolean peutDebiter(BigDecimal montant);
}