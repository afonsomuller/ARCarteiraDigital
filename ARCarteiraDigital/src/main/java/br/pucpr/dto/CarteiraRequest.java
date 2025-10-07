package br.pucpr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarteiraRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Saldo é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo não pode ser negativo")
    private BigDecimal saldo;
}