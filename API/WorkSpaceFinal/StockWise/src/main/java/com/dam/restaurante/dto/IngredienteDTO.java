package com.dam.restaurante.dto;

public class IngredienteDTO {

    private Long id;
    private String nombre;
    private String unidadMedida;
    private Double cantidadStock;
    private String color; // verde, amarillo o rojo
    private Double prioridadBaja;
    private Double prioridadMedia;
    private Double prioridadAlta;
    private String proveedor;
    private String fotoUrl;
    

    public IngredienteDTO(Long id, String nombre, String unidadMedida, Double cantidadStock,
                          Double prioridadBaja, Double prioridadMedia, Double prioridadAlta,
                          String proveedor, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.cantidadStock = cantidadStock;
        this.color = calcularColor(cantidadStock, prioridadBaja, prioridadMedia, prioridadAlta);
        this.proveedor = proveedor;
        this.fotoUrl = fotoUrl;
    }
    
    

    public IngredienteDTO() {
		super();
	}

	private String calcularColor(Double cantidad, Double baja, Double media, Double alta) {
        if (cantidad >= baja) return "verde";
        else if (cantidad >= media) return "amarillo";
        else return "rojo";
    }

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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
    
	
}
