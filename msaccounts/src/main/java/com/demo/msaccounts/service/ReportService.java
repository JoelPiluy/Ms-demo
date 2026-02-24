package com.demo.msaccounts.service;

import com.demo.msaccounts.dto.AccountStatementDto;

import java.time.LocalDate;

public interface ReportService {
    AccountStatementDto getAccountStatement(String clientId, LocalDate startDate, LocalDate endDate);
}
