package com.banque.compte.dto;

import com.banque.compte.entities.Devise;
import com.banque.compte.entities.StatutCompte;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDTO {
    private Long id;
    private String numeroCompte;
    private LocalDateTime dateCreation;
    private BigDecimal solde;
    private StatutCompte statut;
    private Devise devise;
    private Long clientId;
    private String typeCompte;
    private BigDecimal decouvert;
    private Double tauxInteret;
}