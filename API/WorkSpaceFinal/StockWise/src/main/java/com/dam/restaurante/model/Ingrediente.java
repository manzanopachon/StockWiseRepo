package com.dam.restaurante.model;

import jakarta.persistence.*;

@Entity
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String unidadMedida;

    private Double cantidadStock;

    private Double prioridadBaja;  // Verde
    private Double prioridadMedia; // Amarillo
    private Double prioridadAlta;  // Rojo

    private String proveedor;

    @Column(columnDefinition = "TEXT")
    private String fotoUrl;
	public Long getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(name = "restaurante_id")
	private Restaurante restaurante;

//Getters y Setters
	public void setId(Long id) {
		this.id = id;
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

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	

}	