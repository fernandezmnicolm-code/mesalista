package com.mesalista.model.enums;

public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado"),
    CANCELADO("Cancelado");

    private final String displayName;

    EstadoPedido(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
