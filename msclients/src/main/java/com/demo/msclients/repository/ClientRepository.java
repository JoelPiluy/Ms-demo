package com.demo.msclients.repository;

import com.demo.msclients.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clienteId);

    Optional<Client> findByIdentification(String identificacion);

    boolean existsByClientId(String clienteId);

    boolean existsByIdentification(String identificacion);
}
