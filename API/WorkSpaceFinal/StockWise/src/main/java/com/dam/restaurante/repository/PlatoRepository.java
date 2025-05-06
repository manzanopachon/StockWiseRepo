package com.dam.restaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Plato;
import com.dam.restaurante.model.PlatoIngrediente;

public interface PlatoRepository extends JpaRepository<Plato, Long> {
    // Buscar platos por categoría
    List<Plato> findByCategoriaId(Long categoriaId);
    

}
