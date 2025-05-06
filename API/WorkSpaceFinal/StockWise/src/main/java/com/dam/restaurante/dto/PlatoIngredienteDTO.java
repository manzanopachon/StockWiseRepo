package com.dam.restaurante.dto;

public class PlatoIngredienteDTO {
    private Long ingredienteId;
    private String nombre;
    private String unidadMedida;
    private Double cantidadStock;
    private Double cantidadNecesaria;

    public PlatoIngredienteDTO() {}

    public PlatoIngredienteDTO(Long ingredienteId, String nombre, String unidadMedida, Double cantidadStock, Double cantidadNecesaria) {
        this.ingredienteId = ingredienteId;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.cantidadStock = cantidadStock;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    // Getters y Setters
    public Long getIngredienteId() {
        return ingredienteId;
    }

    public void setIngredienteId(Long ingredienteId) {
        this.ingredienteId = ingredienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Double getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(Double cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public Double getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(Double cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }
}
