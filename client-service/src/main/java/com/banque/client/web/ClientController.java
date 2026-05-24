package com.banque.client.web;

import com.banque.client.dto.ClientDTO;
import com.banque.client.entities.Client;
import com.banque.client.services.ClientService;
import com.banque.client.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<ClientDTO> creerClient(@RequestBody Map<String, String> request) {
        ClientDTO clientDTO = ClientDTO.builder()
                .nom(request.get("nom"))
                .prenom(request.get("prenom"))
                .email(request.get("email"))
                .telephone(request.get("telephone"))
                .build();

        ClientDTO client = clientService.creerClient(clientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        ClientDTO client = clientService.getClient(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> clientExists(@PathVariable Long id) {
        boolean exists = clientService.clientExists(id);
        return ResponseEntity.ok(exists);
    }

    // NOUVEAU : Validation du code PIN
    @PostMapping("/validate-pin")
    public ResponseEntity<Map<String, Object>> validatePin(@RequestBody Map<String, Object> request) {
        Long clientId = Long.valueOf(request.get("clientId").toString());
        String codePin = request.get("codePin").toString();

        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Map<String, Object> response = new HashMap<>();

            if (client.getCodePin().equals(codePin)) {
                // Créer le DTO manuellement pour inclure toutes les infos
                ClientDTO clientDTO = ClientDTO.builder()
                        .id(client.getId())
                        .nom(client.getNom())
                        .prenom(client.getPrenom())
                        .email(client.getEmail())
                        .telephone(client.getTelephone())
                        .dateCreation(client.getDateCreation())
                        .build();

                response.put("success", true);
                response.put("client", clientDTO);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Code PIN incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Client non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // NOUVEAU : Inscription d'un nouveau client
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerClient(@RequestBody Map<String, String> request) {
        try {
            String nom = request.get("nom");
            String prenom = request.get("prenom");
            String email = request.get("email");
            String telephone = request.get("telephone");
            String codePin = request.get("codePin");

            // Vérifier si l'email existe déjà
            if (clientRepository.findByEmail(email).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Un compte existe déjà avec cet email");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Valider le code PIN (4 chiffres)
            if (!codePin.matches("\\d{4}")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Le code PIN doit contenir exactement 4 chiffres");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Créer le client
            Client client = Client.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .email(email)
                    .telephone(telephone)
                    .codePin(codePin)
                    .build();

            client = clientRepository.save(client);

            // Créer le DTO pour la réponse
            ClientDTO clientDTO = ClientDTO.builder()
                    .id(client.getId())
                    .nom(client.getNom())
                    .prenom(client.getPrenom())
                    .email(client.getEmail())
                    .telephone(client.getTelephone())
                    .dateCreation(client.getDateCreation())
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("client", clientDTO);
            response.put("message", "Compte créé avec succès ! Votre ID est : " + client.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création du compte : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}