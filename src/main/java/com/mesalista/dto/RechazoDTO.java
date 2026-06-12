package com.mesalista.dto;

import jakarta.validation.constraints.NotBlank;

public class RechazoDTO {

    @NotBlank(message = "El motivo de rechazo es obligatorio")
    private String motivoRechazo;

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
