package com.dam.restaurante.dto;

import com.dam.restaurante.model.Empleado;


public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String puestoTrabajo;
    private RestauranteDTO restaurante; 
    private Boolean validado;

    public EmpleadoDTO(Empleado empleado) {
        this.id = empleado.getId();
        this.nombre = empleado.getNombre();
        this.apellidos = empleado.getApellidos();
        this.correo = empleado.getCorreo();
        this.puestoTrabajo = empleado.getPuestoTrabajo();
        this.restaurante = new RestauranteDTO(empleado.getRestaurante()); 
        this.validado = empleado.getValidado();
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

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getPuestoTrabajo() {
		return puestoTrabajo;
	}

	public void setPuestoTrabajo(String puestoTrabajo) {
		this.puestoTrabajo = puestoTrabajo;
	}

	public Boolean getValidado() {
		return validado;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}

	public RestauranteDTO getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(RestauranteDTO restaurante) {
		this.restaurante = restaurante;
	}

    // Getters y setters
    
}
