package com.banque.operation.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDTO {
    private Long id;
    private String numeroCompte;
    private BigDecimal solde;
    private String statut;
    private String devise;
    private Long clientId;
    private String typeCompte;
}