package com.demo.msaccounts.dto;

import com.demo.msaccounts.domain.entity.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponseDto {

    private Long id;
    private LocalDateTime dateMovement;
    private MovementType typeMovement;
    private BigDecimal valueMov;
    private BigDecimal balance;
    private String accountNumber;
}