package com.dam.restaurante.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dam.restaurante.model.Restaurante;
import com.dam.restaurante.repository.RestauranteRepository;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    public Restaurante crearRestaurante(Restaurante restaurante) {
        return restauranteRepository.save(restaurante);
    }

    public List<Restaurante> obtenerTodos() {
        return restauranteRepository.findAll();
    }

    public Restaurante obtenerPorNombre(String nombre) {
        return restauranteRepository.findByNombre(nombre);
    }
    public Optional<Restaurante> obtenerPorId(Long id) {
        return restauranteRepository.findById(id);
    }
}
