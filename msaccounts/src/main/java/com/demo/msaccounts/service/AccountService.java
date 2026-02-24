package com.demo.msaccounts.service;

import com.demo.msaccounts.dto.AccountRequestDto;
import com.demo.msaccounts.dto.AccountResponseDto;

import java.util.List;

public interface AccountService {

    AccountResponseDto create(AccountRequestDto dto);

    AccountResponseDto findById(Long id);

    AccountResponseDto findByAccountNumber(String accountNumber);

    List<AccountResponseDto> findAll();

    AccountResponseDto update(Long id, AccountRequestDto dto);
}