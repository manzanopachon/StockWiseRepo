package com.dam.restaurante.dto;

import com.dam.restaurante.model.Restaurante;

public class RestauranteDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;

    public RestauranteDTO(Restaurante restaurante) {
        this.id = restaurante.getId();
        this.nombre = restaurante.getNombre();
        this.direccion = restaurante.getDireccion();
        this.telefono = restaurante.getTelefono();
    }
    
    //Getters y Setters

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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

    // Getters y setters...
    
    
}
