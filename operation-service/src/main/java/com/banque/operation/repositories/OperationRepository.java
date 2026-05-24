package com.banque.operation.repositories;

import com.banque.operation.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByNumeroCompteSourceOrderByDateOperationDesc(String numeroCompte);
    List<Operation> findByNumeroCompteDestinationOrderByDateOperationDesc(String numeroCompte);
}