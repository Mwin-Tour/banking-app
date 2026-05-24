package com.banque.operation.services;

import com.banque.operation.client.CompteRestClient;
import com.banque.operation.dto.*;
import com.banque.operation.entities.Operation;
import com.banque.operation.entities.TypeOperation;
import com.banque.operation.repositories.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationService {

    private final OperationRepository operationRepository;
    private final CompteRestClient compteRestClient;

    public OperationDTO crediter(OperationRequest request) {
        // Vérifier que le compte existe via Feign
        CompteDTO compte = compteRestClient.getCompteByNumero(request.getNumeroCompte());
        if (compte == null) {
            throw new RuntimeException("Compte non trouvé: " + request.getNumeroCompte());
        }

        // Créditer via Feign
        compteRestClient.crediter(request.getNumeroCompte(), request.getMontant());

        // Enregistrer l'opération
        Operation operation = Operation.builder()
                .montant(request.getMontant())
                .typeOperation(TypeOperation.CREDIT)
                .description(request.getDescription())
                .numeroCompteSource(request.getNumeroCompte())
                .build();

        operation = operationRepository.save(operation);
        return convertToDTO(operation);
    }

    public OperationDTO debiter(OperationRequest request) {
        // Vérifier que le compte existe via Feign
        CompteDTO compte = compteRestClient.getCompteByNumero(request.getNumeroCompte());
        if (compte == null) {
            throw new RuntimeException("Compte non trouvé: " + request.getNumeroCompte());
        }

        // Débiter via Feign
        compteRestClient.debiter(request.getNumeroCompte(), request.getMontant());

        // Enregistrer l'opération
        Operation operation = Operation.builder()
                .montant(request.getMontant())
                .typeOperation(TypeOperation.DEBIT)
                .description(request.getDescription())
                .numeroCompteSource(request.getNumeroCompte())
                .build();

        operation = operationRepository.save(operation);
        return convertToDTO(operation);
    }

    public OperationDTO transferer(OperationRequest request) {
        if (request.getNumeroCompte().equals(request.getNumeroCompteDestinataire())) {
            throw new RuntimeException("Les comptes source et destination doivent être différents");
        }

        // Vérifier les deux comptes via Feign
        CompteDTO compteSource = compteRestClient.getCompteByNumero(request.getNumeroCompte());
        if (compteSource == null) {
            throw new RuntimeException("Compte source non trouvé: " + request.getNumeroCompte());
        }

        CompteDTO compteDestination = compteRestClient.getCompteByNumero(request.getNumeroCompteDestinataire());
        if (compteDestination == null) {
            throw new RuntimeException("Compte destination non trouvé: " + request.getNumeroCompteDestinataire());
        }

        // Débiter le compte source
        compteRestClient.debiter(request.getNumeroCompte(), request.getMontant());

        // Créditer le compte destination
        compteRestClient.crediter(request.getNumeroCompteDestinataire(), request.getMontant());

        // Enregistrer l'opération
        Operation operation = Operation.builder()
                .montant(request.getMontant())
                .typeOperation(TypeOperation.TRANSFERT)
                .description(request.getDescription())
                .numeroCompteSource(request.getNumeroCompte())
                .numeroCompteDestination(request.getNumeroCompteDestinataire())
                .build();

        operation = operationRepository.save(operation);
        return convertToDTO(operation);
    }

    public List<OperationDTO> getHistoriqueCompte(String numeroCompte) {
        List<Operation> operations = new ArrayList<>();

        // Opérations où le compte est la source
        operations.addAll(operationRepository.findByNumeroCompteSourceOrderByDateOperationDesc(numeroCompte));

        // Opérations où le compte est la destination
        operations.addAll(operationRepository.findByNumeroCompteDestinationOrderByDateOperationDesc(numeroCompte));

        return operations.stream()
                .sorted((o1, o2) -> o2.getDateOperation().compareTo(o1.getDateOperation()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OperationDTO getOperation(Long id) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opération non trouvée avec l'ID: " + id));
        return convertToDTO(operation);
    }

    public List<OperationDTO> getAllOperations() {
        return operationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OperationDTO convertToDTO(Operation operation) {
        return OperationDTO.builder()
                .id(operation.getId())
                .dateOperation(operation.getDateOperation())
                .montant(operation.getMontant())
                .typeOperation(operation.getTypeOperation())
                .description(operation.getDescription())
                .numeroCompteSource(operation.getNumeroCompteSource())
                .numeroCompteDestination(operation.getNumeroCompteDestination())
                .build();
    }
}