package com.demo.msaccounts.controller;

import com.demo.msaccounts.dto.MovementRequestDto;
import com.demo.msaccounts.dto.MovementResponseDto;
import com.demo.msaccounts.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping
    public ResponseEntity<MovementResponseDto> create(@Valid @RequestBody MovementRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MovementResponseDto>> findAll() {
        return ResponseEntity.ok(movementService.findAll());
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<MovementResponseDto>> findByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(movementService.findByAccountNumber(accountNumber));
    }
}

