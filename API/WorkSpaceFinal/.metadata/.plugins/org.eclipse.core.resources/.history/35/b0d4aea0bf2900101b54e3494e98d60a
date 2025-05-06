package com.dam.restaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Ingrediente;
import com.dam.restaurante.model.Plato;
import com.dam.restaurante.model.PlatoIngrediente;

public interface PlatoIngredienteRepository extends JpaRepository<PlatoIngrediente, Long> {
    void deleteByPlatoAndIngrediente(Plato plato, Ingrediente ingrediente);
    List<PlatoIngrediente> findByPlatoId(Long platoId);
    Optional<PlatoIngrediente> findByPlatoIdAndIngredienteId(Long platoId, Long ingredienteId);
	void deleteByPlatoId(Long platoId);
	void deleteByPlato_IdAndIngredienteId(Long platoId, Long ingredienteId); // Cambiado a 'Plato_Id'

}
