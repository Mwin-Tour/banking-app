package com.banque.client.services;

import com.banque.client.dto.ClientDTO;
import com.banque.client.entities.Client;
import com.banque.client.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientDTO creerClient(ClientDTO clientDTO) {
        Client client = Client.builder()
                .nom(clientDTO.getNom())
                .prenom(clientDTO.getPrenom())
                .email(clientDTO.getEmail())
                .telephone(clientDTO.getTelephone())
                .build();

        client = clientRepository.save(client);
        return convertToDTO(client);
    }

    public ClientDTO getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));
        return convertToDTO(client);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean clientExists(Long id) {
        return clientRepository.existsById(id);
    }

    private ClientDTO convertToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .dateCreation(client.getDateCreation())
                .build();
    }
}