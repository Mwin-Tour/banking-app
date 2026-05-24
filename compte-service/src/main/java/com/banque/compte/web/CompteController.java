package com.banque.compte.web;

import com.banque.compte.dto.*;
import com.banque.compte.services.CompteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    @PostMapping("/courant")
    public ResponseEntity<CompteDTO> creerCompteCourant(@RequestBody CompteRequest request) {
        CompteDTO compte = compteService.creerCompteCourant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(compte);
    }

    @PostMapping("/epargne")
    public ResponseEntity<CompteDTO> creerCompteEpargne(@RequestBody CompteRequest request) {
        CompteDTO compte = compteService.creerCompteEpargne(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(compte);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompteDTO> getCompte(@PathVariable Long id) {
        CompteDTO compte = compteService.getCompte(id);
        return ResponseEntity.ok(compte);
    }

    @GetMapping("/numero/{numeroCompte}")
    public ResponseEntity<CompteDTO> getCompteByNumero(@PathVariable String numeroCompte) {
        CompteDTO compte = compteService.getCompteByNumero(numeroCompte);
        return ResponseEntity.ok(compte);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CompteDTO>> getComptesClient(@PathVariable Long clientId) {
        List<CompteDTO> comptes = compteService.getComptesClient(clientId);
        return ResponseEntity.ok(comptes);
    }

    @GetMapping
    public ResponseEntity<List<CompteDTO>> getAllComptes() {
        List<CompteDTO> comptes = compteService.getAllComptes();
        return ResponseEntity.ok(comptes);
    }

    @PutMapping("/crediter")
    public ResponseEntity<CompteDTO> crediter(
            @RequestParam String numeroCompte,
            @RequestParam BigDecimal montant) {
        CompteDTO compte = compteService.crediter(numeroCompte, montant);
        return ResponseEntity.ok(compte);
    }

    @PutMapping("/debiter")
    public ResponseEntity<CompteDTO> debiter(
            @RequestParam String numeroCompte,
            @RequestParam BigDecimal montant) {
        CompteDTO compte = compteService.debiter(numeroCompte, montant);
        return ResponseEntity.ok(compte);
    }
}