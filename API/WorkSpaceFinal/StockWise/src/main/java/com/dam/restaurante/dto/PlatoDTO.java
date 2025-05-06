package com.dam.restaurante.dto;

import java.util.List;

public class PlatoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;
    private Long restauranteId;

    // Opcional: lista de IDs de ingredientes relacionados
    private List<Long> ingredientesIds;

    public PlatoDTO() {
    }

    public PlatoDTO(Long id, String nombre, String descripcion, Double precio, String categoria, Long restauranteId, List<Long> ingredientesIds) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.restauranteId = restauranteId;
        this.ingredientesIds = ingredientesIds;
    }

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }

    public void setPrecio(Double precio) { this.precio = precio; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Long getRestauranteId() { return restauranteId; }

    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }

    public List<Long> getIngredientesIds() { return ingredientesIds; }

    public void setIngredientesIds(List<Long> ingredientesIds) { this.ingredientesIds = ingredientesIds; }
}
