package com.dam.restaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    // Aquí puedes añadir consultas personalizadas si es necesario
	Restaurante findByNombre(String nombre);
}
