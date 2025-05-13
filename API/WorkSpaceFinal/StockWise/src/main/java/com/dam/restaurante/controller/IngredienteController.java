package com.dam.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.restaurante.dto.IngredienteCreateDTO;
import com.dam.restaurante.dto.IngredienteDTO;
import com.dam.restaurante.service.IngredienteService;

@RestController
@RequestMapping("/api/ingredientes")
@CrossOrigin(origins = "*") // permite acceder desde Android
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    @GetMapping
    public List<IngredienteDTO> listarIngredientes() {
        return ingredienteService.obtenerTodos();
    }

    @GetMapping("/restaurante/{restauranteId}")
    public List<IngredienteDTO> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ingredienteService.obtenerIngredientesPorRestaurante(restauranteId);
    }
    

    @GetMapping("/{id}")
    public IngredienteDTO obtenerIngrediente(@PathVariable Long id) {
        return ingredienteService.obtenerPorId(id);
    }
/*
    @PostMapping
    public IngredienteDTO crearIngrediente(@RequestBody IngredienteDTO dto) {
        return ingredienteService.crearIngrediente(dto);
    }
    */
    //Crear Ingrediente
    @PostMapping("/crear")
    public ResponseEntity<IngredienteDTO> crearIngrediente(@RequestBody IngredienteCreateDTO dto) {
        IngredienteDTO creado = ingredienteService.crearIngrediente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    @PutMapping("/{id}")
    public IngredienteDTO actualizarIngrediente(@PathVariable Long id, @RequestBody IngredienteDTO dto) {
        return ingredienteService.actualizarIngrediente(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminarIngrediente(@PathVariable Long id) {
        ingredienteService.eliminarIngrediente(id);
    }
}
