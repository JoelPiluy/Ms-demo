package com.demo.msaccounts.mapper;

import com.demo.msaccounts.domain.entity.Account;
import com.demo.msaccounts.dto.AccountRequestDto;
import com.demo.msaccounts.dto.AccountResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "availableBalance", source = "initialBalance")
    Account toEntity(AccountRequestDto dto);

    AccountResponseDto toResponseDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AccountRequestDto dto, @MappingTarget Account account);
}