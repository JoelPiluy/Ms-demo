package com.demo.msclients.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El género es obligatorio")
    private String gender;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad debe ser positiva")
    private Integer age;

    @NotBlank(message = "La identificación es obligatoria")
    private String identification;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    @NotBlank(message = "El clienteId es obligatorio")
    private String clientId;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @NotNull(message = "El estado es obligatorio")
    private Boolean status;

}
