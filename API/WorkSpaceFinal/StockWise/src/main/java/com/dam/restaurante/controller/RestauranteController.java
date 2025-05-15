package com.dam.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.restaurante.dto.RestauranteDTO;
import com.dam.restaurante.model.Restaurante;
import com.dam.restaurante.service.RestauranteService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    // Crear restaurante
    @PostMapping
    public Restaurante crearRestaurante(@RequestBody Restaurante restaurante) {
        return restauranteService.crearRestaurante(restaurante);
    }

    // Obtener todos los restaurantes
    @GetMapping
    public List<Restaurante> obtenerTodos() {
        return restauranteService.obtenerTodos();
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RestauranteDTO> getRestaurantePorNombre(@PathVariable String nombre) {
        Restaurante restaurante = restauranteService.obtenerPorNombre(nombre);
        if (restaurante != null) {
            return ResponseEntity.ok(new RestauranteDTO(restaurante));
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    // Obtener un restaurante por ID
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDTO> obtenerPorId(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.obtenerPorId(id)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontradoooo"));

        return ResponseEntity.ok(new RestauranteDTO(restaurante));
    }
}
