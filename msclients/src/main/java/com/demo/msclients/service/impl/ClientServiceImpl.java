package com.demo.msclients.service.impl;

import com.demo.msclients.domain.entity.Client;
import com.demo.msclients.domain.exception.BusinessException;
import com.demo.msclients.domain.exception.ResourceNotFoundException;
import com.demo.msclients.dto.ClientRequestDto;
import com.demo.msclients.dto.ClientResponseDto;
import com.demo.msclients.kafka.producer.ClientEventProducer;
import com.demo.msclients.mapper.ClientMapper;
import com.demo.msclients.repository.ClientRepository;
import com.demo.msclients.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clienteRepository;
    private final ClientMapper clienteMapper;
    private final ClientEventProducer clienteEventProducer;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public ClientResponseDto create(ClientRequestDto dto) {
        log.info("Creando cliente con clienteId: {}", dto.getClientId());

        if (clienteRepository.existsByClientId(dto.getClientId())) {
            throw new BusinessException("Ya existe un cliente con clienteId: " + dto.getClientId());
        }
        if (clienteRepository.existsByIdentification(dto.getIdentification())) {
            throw new BusinessException("Ya existe un cliente con identificación: " + dto.getIdentification());
        }

        Client client = clienteMapper.toEntity(dto);
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setCreatedBy("system");//aqui como ejemplo pero usualmente suelo sacar el usuario del jwt

        Client saved = clienteRepository.save(client);
        log.info("Cliente creado con id: {}", saved.getId());

        // Publicar evento a Kafka para que ms-cuentas esté al tanto
        clienteEventProducer.publishClientCreated(saved);

        return clienteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto findById(Long id) {
        return clienteMapper.toResponseDto(getClientById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto findByClientId(String clienteId) {
        return clienteRepository.findByClientId(clienteId)
                .map(clienteMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "clienteId", clienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDto> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ClientResponseDto update(Long id, ClientRequestDto dto) {
        log.info("Actualizando cliente con id: {}", id);
        Client client = getClientById(id);

        if (!client.getClientId().equals(dto.getClientId())
                && clienteRepository.existsByClientId(dto.getClientId())) {
            throw new BusinessException("Ya existe un cliente con clienteId: " + dto.getClientId());
        }

        clienteMapper.updateEntityFromDto(dto, client);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        client.setUpdatedBy("system");//aqui como ejemplo pero usualmente suelo sacar el usuario del jwt

        return clienteMapper.toResponseDto(clienteRepository.save(client));
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando cliente con id: {}", id);
        Client client = getClientById(id);
        clienteRepository.delete(client);
        log.info("Cliente eliminado con id: {}", id);
    }

    private Client getClientById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }
}
