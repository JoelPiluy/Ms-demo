package com.demo.msaccounts.service.impl;

import com.demo.msaccounts.domain.entity.Account;
import com.demo.msaccounts.domain.entity.Movement;
import com.demo.msaccounts.domain.entity.MovementType;
import com.demo.msaccounts.domain.exception.BusinessException;
import com.demo.msaccounts.domain.exception.ResourceNotFoundException;
import com.demo.msaccounts.dto.MovementRequestDto;
import com.demo.msaccounts.dto.MovementResponseDto;
import com.demo.msaccounts.mapper.MovementMapper;
import com.demo.msaccounts.repository.AccountRepository;
import com.demo.msaccounts.repository.MovementRepository;
import com.demo.msaccounts.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementMapper movementMapper;

    @Override
    @Transactional
    public MovementResponseDto create(MovementRequestDto dto) {
        log.info("Registering movement for account: {}", dto.getAccountNumber());

        // 1. Find account
        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", dto.getAccountNumber()));

        // 2. Validate account is active
        if (!account.getStatus()) {
            throw new BusinessException("La cuenta " + dto.getAccountNumber() + " no está activa");
        }

        BigDecimal value = dto.getValue();
        BigDecimal currentBalance = account.getAvailableBalance();

        // 3. F3 — Validate sufficient balance for withdrawals
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal newBalance = currentBalance.add(value);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Saldo no disponible");
            }
        }

        // 4. Calculate new balance and determine movement type
        BigDecimal newBalance = currentBalance.add(value);
        MovementType type = value.compareTo(BigDecimal.ZERO) >= 0 ? MovementType.DEPOSIT : MovementType.WITHDRAWAL;

        // 5. Register movement
        Movement movement = Movement.builder()
                .dateMovement(LocalDateTime.now())
                .typeMovement(type)
                .valueMov(value)
                .balance(newBalance)
                .account(account)
                .createdBy("system")
                .build();

        // 6. Update available balance — atomic with movement in same transaction
        account.setAvailableBalance(newBalance);
        account.setUpdatedBy("system");

        accountRepository.save(account);
        Movement saved = movementRepository.save(movement);

        log.info("Movement registered. Type: {}, Value: {}, New balance: {}", type, value, newBalance);
        return movementMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDto> findAll() {
        return movementRepository.findAll()
                .stream()
                .map(movementMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDto> findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        return movementRepository.findByAccountId(account.getId())
                .stream()
                .map(movementMapper::toResponseDto)
                .toList();
    }
}
