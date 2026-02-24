package com.demo.msaccounts.mapper;

import com.demo.msaccounts.domain.entity.Movement;
import com.demo.msaccounts.dto.MovementResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovementMapper {

    @Mapping(target = "accountNumber", source = "account.accountNumber")
    MovementResponseDto toResponseDto(Movement movement);
}
