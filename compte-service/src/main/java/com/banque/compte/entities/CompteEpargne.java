package com.banque.compte.entities;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("EPARGNE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompteEpargne extends Compte {

    private Double tauxInteret = 0.0;

    @Override
    public boolean peutDebiter(BigDecimal montant) {
        return montant.compareTo(getSolde()) <= 0;
    }

    public BigDecimal calculerInterets() {
        return getSolde().multiply(BigDecimal.valueOf(tauxInteret / 100));
    }

    public void appliquerInterets() {
        BigDecimal interets = calculerInterets();
        crediter(interets);
    }
}