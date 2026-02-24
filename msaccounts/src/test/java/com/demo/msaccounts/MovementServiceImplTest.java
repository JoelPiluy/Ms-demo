package com.demo.msaccounts;

import com.demo.msaccounts.domain.entity.Account;
import com.demo.msaccounts.domain.entity.AccountType;
import com.demo.msaccounts.domain.entity.Movement;
import com.demo.msaccounts.domain.entity.MovementType;
import com.demo.msaccounts.domain.exception.BusinessException;
import com.demo.msaccounts.domain.exception.ResourceNotFoundException;
import com.demo.msaccounts.dto.MovementRequestDto;
import com.demo.msaccounts.dto.MovementResponseDto;
import com.demo.msaccounts.mapper.MovementMapper;
import com.demo.msaccounts.repository.AccountRepository;
import com.demo.msaccounts.repository.MovementRepository;
import com.demo.msaccounts.service.impl.MovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovementService - Unit Tests")
class MovementServiceImplTest {

    @Mock private MovementRepository movementRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MovementMapper movementMapper;

    @InjectMocks
    private MovementServiceImpl movementService;

    private Account account;
    private MovementRequestDto depositRequest;
    private MovementRequestDto withdrawalRequest;
    private MovementRequestDto insufficientFundsRequest;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .accountNumber("478758")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("2000.00"))
                .availableBalance(new BigDecimal("2000.00"))
                .status(true)
                .clientId("joselema")
                .build();

        depositRequest = MovementRequestDto.builder()
                .accountNumber("478758")
                .value(new BigDecimal("500.00"))
                .build();

        withdrawalRequest = MovementRequestDto.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-575.00"))
                .build();

        insufficientFundsRequest = MovementRequestDto.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-9999.00"))
                .build();
    }

    @Test
    @DisplayName("F2 - Should register a deposit and update balance")
    void create_shouldRegisterDeposit_andUpdateBalance() {
        MovementResponseDto responseDto = MovementResponseDto.builder()
                .id(1L).typeMovement(MovementType.DEPOSIT)
                .valueMov(new BigDecimal("500.00"))
                .balance(new BigDecimal("2500.00"))
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenReturn(new Movement());
        when(movementMapper.toResponseDto(any())).thenReturn(responseDto);

        MovementResponseDto result = movementService.create(depositRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTypeMovement()).isEqualTo(MovementType.DEPOSIT);
        assertThat(account.getAvailableBalance()).isEqualByComparingTo("2500.00");
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("F2 - Should register a withdrawal and update balance")
    void create_shouldRegisterWithdrawal_andUpdateBalance() {
        MovementResponseDto responseDto = MovementResponseDto.builder()
                .id(1L).typeMovement(MovementType.WITHDRAWAL)
                .valueMov(new BigDecimal("-575.00"))
                .balance(new BigDecimal("1425.00"))
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenReturn(new Movement());
        when(movementMapper.toResponseDto(any())).thenReturn(responseDto);

        MovementResponseDto result = movementService.create(withdrawalRequest);

        assertThat(result.getTypeMovement()).isEqualTo(MovementType.WITHDRAWAL);
        assertThat(account.getAvailableBalance()).isEqualByComparingTo("1425.00");
    }

    @Test
    @DisplayName("F3 - Should throw BusinessException with 'Saldo no disponible' when insufficient funds")
    void create_shouldThrowBusinessException_whenInsufficientFunds() {
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> movementService.create(insufficientFundsRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Saldo no disponible");

        verify(movementRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account does not exist")
    void create_shouldThrowNotFoundException_whenAccountNotFound() {
        when(accountRepository.findByAccountNumber("000000")).thenReturn(Optional.empty());

        MovementRequestDto request = MovementRequestDto.builder()
                .accountNumber("000000").value(new BigDecimal("100")).build();

        assertThatThrownBy(() -> movementService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account");
    }
}
