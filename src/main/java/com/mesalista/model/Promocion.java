package com.mesalista.model;

import com.mesalista.model.enums.EstadoValidacion;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    private Integer porcentajeDescuento;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal precioPromocional;

    @Column(length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoValidacion estadoValidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aliado_id", nullable = false)
    private Usuario aliadoGastronomico;

    @PrePersist
    protected void onCreate() {
        if (estadoValidacion == null) {
            estadoValidacion = EstadoValidacion.PENDIENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(Integer porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public java.math.BigDecimal getPrecioPromocional() {
        return precioPromocional;
    }

    public void setPrecioPromocional(java.math.BigDecimal precioPromocional) {
        this.precioPromocional = precioPromocional;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoValidacion getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(EstadoValidacion estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public Usuario getAliadoGastronomico() {
        return aliadoGastronomico;
    }

    public void setAliadoGastronomico(Usuario aliadoGastronomico) {
        this.aliadoGastronomico = aliadoGastronomico;
    }
}
