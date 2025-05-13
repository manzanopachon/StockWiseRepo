package com.dam.restaurante.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    private Integer numeroMesa;
    private Long restauranteId;
    private List<Long> platos;
    private LocalDateTime fechaHora;  // Agregamos la fecha y hora

    // Getters y setters
    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public List<Long> getPlatos() {
        return platos;
    }

    public void setPlatos(List<Long> platos) {
        this.platos = platos;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    
}
