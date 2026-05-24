package com.banque.compte.services;

import com.banque.compte.client.ClientRestClient;
import com.banque.compte.dto.*;
import com.banque.compte.entities.*;
import com.banque.compte.repositories.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRestClient clientRestClient;

    public CompteDTO creerCompteCourant(CompteRequest request) {
        // Vérifier que le client existe via Feign
        Boolean clientExists = clientRestClient.clientExists(request.getClientId());
        if (clientExists == null || !clientExists) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + request.getClientId());
        }

        CompteCourant compte = new CompteCourant();
        compte.setClientId(request.getClientId());
        compte.setDevise(request.getDevise());
        compte.setDecouvert(request.getDecouvert());
        compte.setStatut(StatutCompte.ACTIF);

        compte = (CompteCourant) compteRepository.save(compte);
        return convertToDTO(compte);
    }

    public CompteDTO creerCompteEpargne(CompteRequest request) {
        // Vérifier que le client existe via Feign
        Boolean clientExists = clientRestClient.clientExists(request.getClientId());
        if (clientExists == null || !clientExists) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + request.getClientId());
        }

        CompteEpargne compte = new CompteEpargne();
        compte.setClientId(request.getClientId());
        compte.setDevise(request.getDevise());
        compte.setTauxInteret(request.getTauxInteret());
        compte.setStatut(StatutCompte.ACTIF);

        compte = (CompteEpargne) compteRepository.save(compte);
        return convertToDTO(compte);
    }

    public CompteDTO getCompte(Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        return convertToDTO(compte);
    }

    public CompteDTO getCompteByNumero(String numeroCompte) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + numeroCompte));
        return convertToDTO(compte);
    }

    public List<CompteDTO> getComptesClient(Long clientId) {
        return compteRepository.findByClientId(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CompteDTO> getAllComptes() {
        return compteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CompteDTO crediter(String numeroCompte, java.math.BigDecimal montant) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        compte.crediter(montant);
        compte = compteRepository.save(compte);
        return convertToDTO(compte);
    }

    public CompteDTO debiter(String numeroCompte, java.math.BigDecimal montant) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        compte.debiter(montant);
        compte = compteRepository.save(compte);
        return convertToDTO(compte);
    }

    private CompteDTO convertToDTO(Compte compte) {
        CompteDTO dto = CompteDTO.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .dateCreation(compte.getDateCreation())
                .solde(compte.getSolde())
                .statut(compte.getStatut())
                .devise(compte.getDevise())
                .clientId(compte.getClientId())
                .build();

        if (compte instanceof CompteCourant) {
            dto.setTypeCompte("COURANT");
            dto.setDecouvert(((CompteCourant) compte).getDecouvert());
        } else if (compte instanceof CompteEpargne) {
            dto.setTypeCompte("EPARGNE");
            dto.setTauxInteret(((CompteEpargne) compte).getTauxInteret());
        }

        return dto;
    }
}