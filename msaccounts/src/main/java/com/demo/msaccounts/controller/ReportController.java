package com.demo.msaccounts.controller;

import com.demo.msaccounts.dto.AccountStatementDto;
import com.demo.msaccounts.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<AccountStatementDto> getAccountStatement(
            @RequestParam("date") String dateRange,
            @RequestParam("client") String clientId) {

        // Parse "startDate,endDate" format
        String[] dates = dateRange.split(",");
        if (dates.length != 2) {
            throw new IllegalArgumentException("El parámetro fecha debe tener el formato: yyyy-MM-dd,yyyy-MM-dd");
        }

        LocalDate startDate = LocalDate.parse(dates[0].trim());
        LocalDate endDate = LocalDate.parse(dates[1].trim());

        return ResponseEntity.ok(reportService.getAccountStatement(clientId, startDate, endDate));
    }
}

