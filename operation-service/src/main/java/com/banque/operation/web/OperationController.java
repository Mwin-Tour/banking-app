package com.banque.operation.web;

import com.banque.operation.dto.*;
import com.banque.operation.services.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/credit")
    public ResponseEntity<OperationDTO> crediter(@RequestBody OperationRequest request) {
        OperationDTO operation = operationService.crediter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @PostMapping("/debit")
    public ResponseEntity<OperationDTO> debiter(@RequestBody OperationRequest request) {
        OperationDTO operation = operationService.debiter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @PostMapping("/transfert")
    public ResponseEntity<OperationDTO> transferer(@RequestBody OperationRequest request) {
        OperationDTO operation = operationService.transferer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @GetMapping("/compte/{numeroCompte}")
    public ResponseEntity<List<OperationDTO>> getHistoriqueCompte(@PathVariable String numeroCompte) {
        List<OperationDTO> operations = operationService.getHistoriqueCompte(numeroCompte);
        return ResponseEntity.ok(operations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationDTO> getOperation(@PathVariable Long id) {
        OperationDTO operation = operationService.getOperation(id);
        return ResponseEntity.ok(operation);
    }

    @GetMapping
    public ResponseEntity<List<OperationDTO>> getAllOperations() {
        List<OperationDTO> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }
}