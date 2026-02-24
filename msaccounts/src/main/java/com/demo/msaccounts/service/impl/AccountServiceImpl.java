package com.demo.msaccounts.service.impl;

import com.demo.msaccounts.domain.entity.Account;
import com.demo.msaccounts.domain.exception.BusinessException;
import com.demo.msaccounts.domain.exception.ResourceNotFoundException;
import com.demo.msaccounts.dto.AccountRequestDto;
import com.demo.msaccounts.dto.AccountResponseDto;
import com.demo.msaccounts.mapper.AccountMapper;
import com.demo.msaccounts.repository.AccountRepository;
import com.demo.msaccounts.repository.ClientRefRepository;
import com.demo.msaccounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientRefRepository clientRefRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponseDto create(AccountRequestDto dto) {
        log.info("Creating account: {}", dto.getAccountNumber());

        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new BusinessException("Ya existe una cuenta con número: " + dto.getAccountNumber());
        }
        if (!clientRefRepository.existsByClientId(dto.getClientId())) {
            throw new ResourceNotFoundException("Client", "clientId", dto.getClientId());
        }

        Account account = accountMapper.toEntity(dto);
        account.setCreatedBy("system");

        return accountMapper.toResponseDto(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto findById(Long id) {
        return accountMapper.toResponseDto(getAccountById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDto> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponseDto update(Long id, AccountRequestDto dto) {
        log.info("Updating account with id: {}", id);
        Account account = getAccountById(id);
        accountMapper.updateEntityFromDto(dto, account);
        account.setUpdatedBy("system");
        return accountMapper.toResponseDto(accountRepository.save(account));
    }

    private Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
    }
}