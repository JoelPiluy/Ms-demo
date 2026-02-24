package com.demo.msclients.service;

import com.demo.msclients.dto.ClientRequestDto;
import com.demo.msclients.dto.ClientResponseDto;

import java.util.List;

public interface ClientService {
    ClientResponseDto create(ClientRequestDto dto);

    ClientResponseDto findById(Long id);

    ClientResponseDto findByClientId(String clienteId);

    List<ClientResponseDto> findAll();

    ClientResponseDto update(Long id, ClientRequestDto dto);

    void delete(Long id);
}
