package com.demo.msclients.mapper;

import com.demo.msclients.domain.entity.Client;
import com.demo.msclients.dto.ClientRequestDto;
import com.demo.msclients.dto.ClientResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {
    Client toEntity(ClientRequestDto dto);

    ClientResponseDto toResponseDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClientRequestDto dto, @MappingTarget Client client);
}
