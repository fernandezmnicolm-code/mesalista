package com.mesalista.model.enums;

public enum CategoriaProducto {
    ENTRADA("Entradas"),
    PLATO("Plato del Menú"),
    CAFE("Café y Bebidas"),
    TORTA("Tortas y Postres"),
    COMBO("Combo"),
    PROMOCION("Promoción"),
    RESERVA("Reserva de Mesa"),
    EVENTO("Servicio para Eventos"),
    RECOJO("Pedido para Recoger"),
    GENERAL("General");

    private final String displayName;

    CategoriaProducto(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
