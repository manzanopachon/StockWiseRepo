package com.dam.restaurante.dto;

public class CategoriaDTO {
    private Long id;
    private String nombre;

    // Constructor vacío, getters y setters
    

    public CategoriaDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public CategoriaDTO(String nombre) {
		super();
		this.nombre = nombre;
	}

	public CategoriaDTO() {
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
}
