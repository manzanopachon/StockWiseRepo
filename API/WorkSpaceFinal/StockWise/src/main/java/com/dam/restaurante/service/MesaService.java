package com.dam.restaurante.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dam.restaurante.repository.MesaRepository;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public Optional<Long> getRestauranteIdByNumeroMesa(Integer numeroMesa, Long restauranteID) {
        return mesaRepository.findByNumeroAndRestauranteId(numeroMesa,restauranteID)
                .map(mesa -> mesa.getRestaurante().getId());
    }
}
