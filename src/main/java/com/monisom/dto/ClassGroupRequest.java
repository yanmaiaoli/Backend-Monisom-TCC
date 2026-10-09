package com.monisom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClassGroupRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "matutino|vespertino|noturno") String turno,
        @NotNull @Min(0) Integer capacidade) {
}
