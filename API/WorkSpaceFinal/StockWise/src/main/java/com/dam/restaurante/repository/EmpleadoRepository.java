package com.dam.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Buscar por correo
    Empleado findByCorreo(String correo);
}
