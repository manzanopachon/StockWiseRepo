package com.dam.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Long> {
    // Buscar platos por categoría
    List<Plato> findByCategoriaId(Long categoriaId);
   // List<Plato> findAllById(List<Long> ids);

}
