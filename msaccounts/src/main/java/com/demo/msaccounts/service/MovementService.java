package com.demo.msaccounts.service;

import com.demo.msaccounts.dto.MovementRequestDto;
import com.demo.msaccounts.dto.MovementResponseDto;

import java.util.List;

public interface MovementService {

    MovementResponseDto create(MovementRequestDto dto);

    List<MovementResponseDto> findAll();

    List<MovementResponseDto> findByAccountNumber(String accountNumber);
}