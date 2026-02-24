package com.demo.msclients;

import com.demo.msclients.domain.entity.Client;
import com.demo.msclients.domain.exception.BusinessException;
import com.demo.msclients.domain.exception.ResourceNotFoundException;
import com.demo.msclients.dto.ClientRequestDto;
import com.demo.msclients.dto.ClientResponseDto;
import com.demo.msclients.kafka.producer.ClientEventProducer;
import com.demo.msclients.mapper.ClientMapper;
import com.demo.msclients.repository.ClientRepository;
import com.demo.msclients.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService - Unit Tests")
class ClientServiceImplTest {
    @Mock private ClientRepository clientRepository;
    @Mock private ClientMapper clientMapper;
    @Mock private ClientEventProducer clientEventProducer;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientRequestDto requestDto;
    private Client client;
    private ClientResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = ClientRequestDto.builder()
                .name("Jose Lema")
                .gender("Masculino")
                .age(30)
                .identification("1234567890")
                .address("Otavalo sn y director")
                .phone("098254785")
                .clientId("joselema")
                .password("1234")
                .status(true)
                .build();

        client = new Client();
        client.setId(1L);
        client.setName("Jose Lema");
        client.setClientId("joselema");
        client.setIdentification("1234567890");
        client.setStatus(true);

        responseDto = ClientResponseDto.builder()
                .id(1L)
                .name("Jose Lema")
                .clientId("joselema")
                .identification("1234567890")
                .status(true)
                .build();
    }

    @Test
    @DisplayName("Should create a client successfully")
    void create_shouldReturnClientResponseDto_whenDataIsValid() {
        when(clientRepository.existsByClientId(anyString())).thenReturn(false);
        when(clientRepository.existsByIdentification(anyString())).thenReturn(false);
        when(clientMapper.toEntity(any(ClientRequestDto.class))).thenReturn(client);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        ClientResponseDto result = clientService.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jose Lema");
        assertThat(result.getClientId()).isEqualTo("joselema");

        verify(clientRepository).save(any(Client.class));
        verify(clientEventProducer).publishClientCreated(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when clientId already exists")
    void create_shouldThrowBusinessException_whenClientIdIsDuplicated() {
        when(clientRepository.existsByClientId("joselema")).thenReturn(true);

        assertThatThrownBy(() -> clientService.create(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("joselema");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return list of all clients")
    void findAll_shouldReturnClientList() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        List<ClientResponseDto> result = clientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jose Lema");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client does not exist")
    void findById_shouldThrowNotFoundException_whenClientDoesNotExist() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client");
    }

    @Test
    @DisplayName("Should delete client successfully")
    void delete_shouldDeleteClient_whenClientExists() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(any(Client.class));

        assertThatCode(() -> clientService.delete(1L)).doesNotThrowAnyException();

        verify(clientRepository).delete(client);
    }
}
