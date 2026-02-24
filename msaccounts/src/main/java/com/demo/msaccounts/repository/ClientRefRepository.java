package com.demo.msaccounts.repository;

import com.demo.msaccounts.domain.entity.ClientRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRefRepository extends JpaRepository<ClientRef, Long> {

    Optional<ClientRef> findByClientId(String clientId);

    boolean existsByClientId(String clientId);
}
