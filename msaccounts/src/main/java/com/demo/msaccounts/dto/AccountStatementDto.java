package com.demo.msaccounts.dto;

import com.demo.msaccounts.domain.entity.AccountType;
import com.demo.msaccounts.domain.entity.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {
    private String clientName;
    private String clientId;
    private List<AccountDetail> accounts;

    @Data
    @Builder
    public static class AccountDetail {
        private String accountNumber;
        private AccountType accountType;
        private BigDecimal initialBalance;
        private BigDecimal availableBalance;
        private Boolean status;
        private List<MovementDetail> movements;
    }

    @Data
    @Builder
    public static class MovementDetail {
        private LocalDateTime date;
        private MovementType type;
        private BigDecimal value;
        private BigDecimal balance;
    }
}
