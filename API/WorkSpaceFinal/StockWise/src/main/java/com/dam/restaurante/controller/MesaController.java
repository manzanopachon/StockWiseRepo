package com.dam.restaurante.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.restaurante.model.Mesa;
import com.dam.restaurante.repository.MesaRepository;
import com.dam.restaurante.service.MesaService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;
    
    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping("/restaurante-id")
    public ResponseEntity<Long> getRestauranteIdFromMesa(
        @RequestParam int numeroMesa,
        @RequestParam Long restauranteId) {

        Optional<Mesa> mesa = mesaRepository.findByNumeroAndRestauranteId(numeroMesa, restauranteId);

        if (mesa.isPresent()) {
            return ResponseEntity.ok(mesa.get().getRestaurante().getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
