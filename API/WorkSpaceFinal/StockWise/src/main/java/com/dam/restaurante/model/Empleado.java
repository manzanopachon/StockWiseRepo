package com.dam.restaurante.model;

import jakarta.persistence.*;

@Entity
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellidos;

    @Column(unique = true)
    private String correo;

    private String contraseña;

    private String puestoTrabajo;

    private String codigoValidacion;

    private Boolean validado = false;
    
    private int intentosRestantes = 3;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

   
    // Getters y setters
    
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

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	public String getPuestoTrabajo() {
		return puestoTrabajo;
	}

	public void setPuestoTrabajo(String puestoTrabajo) {
		this.puestoTrabajo = puestoTrabajo;
	}

	public String getCodigoValidacion() {
		return codigoValidacion;
	}

	public void setCodigoValidacion(String codigoValidacion) {
		this.codigoValidacion = codigoValidacion;
	}

	public Boolean getValidado() {
		return validado;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	public int getIntentosRestantes() {
		return intentosRestantes;
	}

	public void setIntentosRestantes(int intentosRestantes) {
		this.intentosRestantes = intentosRestantes;
	}

    
    
}