package com.dam.restaurante.dto;

public class IngredienteCreateDTO {
    private String nombre;
    private String unidadMedida;
    private Double cantidadStock;
    private Double prioridadBaja;
    private Double prioridadMedia;
    private Double prioridadAlta;
    private String proveedor;
    private String fotoUrl;
    private Long restauranteId; // este es clave
    
    // Getters y setters
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
	public Double getPrioridadBaja() {
		return prioridadBaja;
	}
	public void setPrioridadBaja(Double prioridadBaja) {
		this.prioridadBaja = prioridadBaja;
	}
	public Double getPrioridadMedia() {
		return prioridadMedia;
	}
	public void setPrioridadMedia(Double prioridadMedia) {
		this.prioridadMedia = prioridadMedia;
	}
	public Double getPrioridadAlta() {
		return prioridadAlta;
	}
	public void setPrioridadAlta(Double prioridadAlta) {
		this.prioridadAlta = prioridadAlta;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public String getFotoUrl() {
		return fotoUrl;
	}
	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}
	public Long getRestauranteId() {
		return restauranteId;
	}
	public void setRestauranteId(Long restauranteId) {
		this.restauranteId = restauranteId;
	}

   
    
    
}
