package com.demo.msclients.domain.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "client")
public class Client extends Person {
    @NotBlank(message = "El clienteId es obligatorio")
    @Column(name = "client_id", nullable = false, unique = true, length = 50)
    private String clientId;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String password;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean status;
}
