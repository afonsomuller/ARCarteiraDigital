package br.pucpr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoedaRequest {

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 3, message = "Código deve ter 3 caracteres")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String simbolo;
    private String codigoBcb;
    private Boolean ativa = true;
}