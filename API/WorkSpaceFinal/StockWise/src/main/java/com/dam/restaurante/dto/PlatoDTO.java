package com.dam.restaurante.dto;

import java.util.List;

public class PlatoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private CategoriaDTO categoria;
    private Long restauranteId;
    
    // Nuevo: lista de ingredientes con cantidad
    private List<IngredienteCantidadDTO> ingredientes;

    // Constructor, getters y setters

    public static class IngredienteCantidadDTO {
        private Long ingredienteId;
        private Double cantidad;
		public Long getIngredienteId() {
			return ingredienteId;
		}
		public void setIngredienteId(Long ingredienteId) {
			this.ingredienteId = ingredienteId;
		}
		public Double getCantidad() {
			return cantidad;
		}
		public void setCantidad(Double cantidad) {
			this.cantidad = cantidad;
		}
		public IngredienteCantidadDTO(Long ingredienteId, Double cantidad) {
			super();
			this.ingredienteId = ingredienteId;
			this.cantidad = cantidad;
		}
		public IngredienteCantidadDTO() {
			super();
		}

        // Constructor vacío, getters y setters
        
    }
    
    

	public PlatoDTO(Long id, String nombre, String descripcion, Double precio, CategoriaDTO categoria,
			Long restauranteId, List<IngredienteCantidadDTO> ingredientes) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.categoria = categoria;
		this.restauranteId = restauranteId;
		this.ingredientes = ingredientes;
	}

	public PlatoDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public CategoriaDTO getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaDTO categoria) {
		this.categoria = categoria;
	}

	public Long getRestauranteId() {
		return restauranteId;
	}

	public void setRestauranteId(Long restauranteId) {
		this.restauranteId = restauranteId;
	}

	public List<IngredienteCantidadDTO> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<IngredienteCantidadDTO> ingredientes) {
		this.ingredientes = ingredientes;
	}
}

