package com.banque.compte.client;

import com.banque.compte.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface ClientRestClient {

    @GetMapping("/api/clients/{id}")
    ClientDTO getClient(@PathVariable Long id);

    @GetMapping("/api/clients/{id}/exists")
    Boolean clientExists(@PathVariable Long id);
}