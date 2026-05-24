package com.banque.operation.dto;

import com.banque.operation.entities.TypeOperation;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationDTO {
    private Long id;
    private LocalDateTime dateOperation;
    private BigDecimal montant;
    private TypeOperation typeOperation;
    private String description;
    private String numeroCompteSource;
    private String numeroCompteDestination;
}