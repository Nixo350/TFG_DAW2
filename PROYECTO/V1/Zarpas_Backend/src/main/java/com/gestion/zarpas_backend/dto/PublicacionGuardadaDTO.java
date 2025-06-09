package com.gestion.zarpas_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionGuardadaDTO {
    private Long idPublicacion;
    private LocalDateTime fechaGuardado;
}