package com.dam.restaurante.dto;

public class CodigoVerificacionRequest {
    private Long empleadoId;
    private String codigo;
    // Getters y setters
	public Long getEmpleadoId() {
		return empleadoId;
	}
	public void setEmpleadoId(Long empleadoId) {
		this.empleadoId = empleadoId;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
    
    
}