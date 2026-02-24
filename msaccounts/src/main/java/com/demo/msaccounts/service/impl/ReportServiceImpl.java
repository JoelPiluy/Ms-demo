package com.demo.msaccounts.service.impl;

import com.demo.msaccounts.domain.entity.Account;
import com.demo.msaccounts.domain.entity.Movement;
import com.demo.msaccounts.domain.exception.ResourceNotFoundException;
import com.demo.msaccounts.dto.AccountStatementDto;
import com.demo.msaccounts.repository.AccountRepository;
import com.demo.msaccounts.repository.ClientRefRepository;
import com.demo.msaccounts.repository.MovementRepository;
import com.demo.msaccounts.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ClientRefRepository clientRefRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountStatementDto getAccountStatement(String clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating account statement for clientId: {} from {} to {}", clientId, startDate, endDate);

        // 1. Find client from local ref — no call to ms-clients needed
        var clientRef = clientRefRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));

        // 2. Find all accounts for the client
        List<Account> accounts = accountRepository.findByClientId(clientId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 3. Build account details with movements in date range
        List<AccountStatementDto.AccountDetail> accountDetails = accounts.stream()
                .map(account -> {
                    List<Movement> movements = movementRepository.findByAccountIdAndDateRange(
                            account.getId(), startDateTime, endDateTime);

                    List<AccountStatementDto.MovementDetail> movementDetails = movements.stream()
                            .map(m -> AccountStatementDto.MovementDetail.builder()
                                    .date(m.getDateMovement())
                                    .type(m.getTypeMovement())
                                    .value(m.getValueMov())
                                    .balance(m.getBalance())
                                    .build())
                            .toList();

                    return AccountStatementDto.AccountDetail.builder()
                            .accountNumber(account.getAccountNumber())
                            .accountType(account.getAccountType())
                            .initialBalance(account.getInitialBalance())
                            .availableBalance(account.getAvailableBalance())
                            .status(account.getStatus())
                            .movements(movementDetails)
                            .build();
                })
                .toList();

        return AccountStatementDto.builder()
                .clientId(clientRef.getClientId())
                .clientName(clientRef.getName())
                .accounts(accountDetails)
                .build();
    }
}