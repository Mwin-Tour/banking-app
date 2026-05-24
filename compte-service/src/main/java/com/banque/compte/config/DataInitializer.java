package com.banque.compte.config;

import com.banque.compte.entities.*;
import com.banque.compte.repositories.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CompteRepository compteRepository;

    @Bean
    CommandLineRunner initCompteDatabase() {
        return args -> {
            CompteCourant cc1 = new CompteCourant();
            cc1.setClientId(1L);
            cc1.setDevise(Devise.FCFA);
            cc1.setDecouvert(new BigDecimal("50000"));
            cc1.setStatut(StatutCompte.ACTIF);
            cc1.setSolde(new BigDecimal("100000"));
            compteRepository.save(cc1);

            CompteEpargne ce1 = new CompteEpargne();
            ce1.setClientId(1L);
            ce1.setDevise(Devise.FCFA);
            ce1.setTauxInteret(5.5);
            ce1.setStatut(StatutCompte.ACTIF);
            ce1.setSolde(new BigDecimal("500000"));
            compteRepository.save(ce1);

            System.out.println("=== COMPTE-SERVICE: 2 comptes créés ===");
            System.out.println("Compte Courant: " + cc1.getNumeroCompte());
            System.out.println("Compte Epargne: " + ce1.getNumeroCompte());
        };
    }
}