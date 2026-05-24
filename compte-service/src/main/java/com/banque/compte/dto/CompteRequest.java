package com.banque.compte.dto;

import com.banque.compte.entities.Devise;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteRequest {
    private Long clientId;
    private Devise devise;
    private BigDecimal decouvert;
    private Double tauxInteret;
}