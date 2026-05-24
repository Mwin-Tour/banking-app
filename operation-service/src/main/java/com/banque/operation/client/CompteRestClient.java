package com.banque.operation.client;

import com.banque.operation.dto.CompteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@FeignClient(name = "compte-service")
public interface CompteRestClient {

    @GetMapping("/api/comptes/numero/{numeroCompte}")
    CompteDTO getCompteByNumero(@PathVariable String numeroCompte);

    @PutMapping("/api/comptes/crediter")
    CompteDTO crediter(@RequestParam String numeroCompte, @RequestParam BigDecimal montant);

    @PutMapping("/api/comptes/debiter")
    CompteDTO debiter(@RequestParam String numeroCompte, @RequestParam BigDecimal montant);
}