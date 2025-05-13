package com.dam.restaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
	Optional<Mesa> findByNumeroAndRestauranteId(int numero, Long restauranteId);

}

