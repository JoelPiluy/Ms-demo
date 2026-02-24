package com.demo.msaccounts.repository;

import com.demo.msaccounts.domain.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountId(Long accountId);

    // Query optimizada para el reporte F4 — usa el índice compuesto idx_movement_account_date
    @Query("SELECT m FROM Movement m WHERE m.account.id = :accountId " +
            "AND m.dateMovement BETWEEN :startDate AND :endDate " +
            "ORDER BY m.dateMovement DESC")
    List<Movement> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
