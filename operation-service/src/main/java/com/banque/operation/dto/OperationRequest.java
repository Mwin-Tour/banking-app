package com.banque.operation.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequest {
    private String numeroCompte;
    private BigDecimal montant;
    private String description;
    private String numeroCompteDestinataire;
}