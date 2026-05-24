package com.banque.compte.entities;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("COURANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompteCourant extends Compte {

    @Column(precision = 19, scale = 2)
    private BigDecimal decouvert = BigDecimal.ZERO;

    @Override
    public boolean peutDebiter(BigDecimal montant) {
        BigDecimal soldeAvecDecouvert = getSolde().add(decouvert);
        return montant.compareTo(soldeAvecDecouvert) <= 0;
    }
}