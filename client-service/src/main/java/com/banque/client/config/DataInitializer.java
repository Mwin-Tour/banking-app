package com.banque.client.config;

import com.banque.client.entities.Client;
import com.banque.client.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ClientRepository clientRepository;

    @Bean
    CommandLineRunner initClientDatabase() {
        return args -> {
            Client client1 = Client.builder()
                    .nom("OUEDRAOGO")
                    .prenom("Jean")
                    .email("jean.ouedraogo@email.com")
                    .telephone("+226 70 12 34 56")
                    .codePin("1234")  // Code PIN
                    .build();
            clientRepository.save(client1);

            Client client2 = Client.builder()
                    .nom("KABORE")
                    .prenom("Marie")
                    .email("marie.kabore@email.com")
                    .telephone("+226 76 98 76 54")
                    .codePin("5678")  // Code PIN
                    .build();
            clientRepository.save(client2);

            Client client3 = Client.builder()
                    .nom("SAWADOGO")
                    .prenom("Pierre")
                    .email("pierre.sawadogo@email.com")
                    .telephone("+226 78 45 67 89")
                    .codePin("9999")  // Code PIN
                    .build();
            clientRepository.save(client3);

            System.out.println("========================================");
            System.out.println("CLIENT SERVICE - Données initialisées");
            System.out.println("========================================");
            System.out.println("3 clients créés :");
            System.out.println("- Client ID 1 : " + client1.getNom() + " " + client1.getPrenom() + " | PIN: 1234");
            System.out.println("- Client ID 2 : " + client2.getNom() + " " + client2.getPrenom() + " | PIN: 5678");
            System.out.println("- Client ID 3 : " + client3.getNom() + " " + client3.getPrenom() + " | PIN: 9999");
            System.out.println("========================================");
        };
    }
}