package com.dam.restaurante.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dam.restaurante.dto.IngredienteCreateDTO;
import com.dam.restaurante.dto.IngredienteDTO;
import com.dam.restaurante.model.Ingrediente;
import com.dam.restaurante.model.Restaurante;
import com.dam.restaurante.repository.IngredienteRepository;
import com.dam.restaurante.repository.RestauranteRepository;

@Service
public class IngredienteService {

    @Autowired
    private IngredienteRepository ingredienteRepository;
    
    @Autowired
    private RestauranteRepository restauranteRepository;

    public List<IngredienteDTO> obtenerTodos() {
        return ingredienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<IngredienteDTO> obtenerIngredientesPorRestaurante(Long restauranteId) {
        return ingredienteRepository.findByRestauranteId(restauranteId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public IngredienteDTO obtenerPorId(Long id) {
        return ingredienteRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public IngredienteDTO crearIngrediente(IngredienteDTO dto) {
        Ingrediente ingrediente = toEntity(dto);
        System.out.println("Datos recibidos:");
        System.out.println("Nombre: " + ingrediente.getNombre());
        System.out.println("Cantidad: " + ingrediente.getCantidadStock());
        System.out.println("Unidad Medida: " + ingrediente.getUnidadMedida());
        System.out.println("Prioridad Baja: " + ingrediente.getPrioridadBaja());
        System.out.println("Prioridad Media: " + ingrediente.getPrioridadMedia());
        System.out.println("Prioridad Alta: " + ingrediente.getPrioridadAlta());
        System.out.println("Proveedor: " + ingrediente.getProveedor());
        System.out.println("Foto URL: " + ingrediente.getFotoUrl());
        
        return toDTO(ingredienteRepository.save(ingrediente));
    }

    public IngredienteDTO actualizarIngrediente(Long id, IngredienteDTO dto) {
        Optional<Ingrediente> opt = ingredienteRepository.findById(id);
        if (opt.isPresent()) {
            Ingrediente ingrediente = opt.get();
            ingrediente.setNombre(dto.getNombre());
            ingrediente.setUnidadMedida(dto.getUnidadMedida());
            ingrediente.setCantidadStock(dto.getCantidadStock());
            ingrediente.setPrioridadBaja(dto.getPrioridadBaja());
            ingrediente.setPrioridadMedia(dto.getPrioridadMedia());
            ingrediente.setPrioridadAlta(dto.getPrioridadAlta());
            ingrediente.setProveedor(dto.getProveedor());
            ingrediente.setFotoUrl(dto.getFotoUrl());
            return toDTO(ingredienteRepository.save(ingrediente));
        }
        return null;
    }

    public IngredienteDTO crearIngrediente(IngredienteCreateDTO dto) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(dto.getNombre());
        ingrediente.setUnidadMedida(dto.getUnidadMedida());
        ingrediente.setCantidadStock(dto.getCantidadStock());
        ingrediente.setPrioridadBaja(dto.getPrioridadBaja());
        ingrediente.setPrioridadMedia(dto.getPrioridadMedia());
        ingrediente.setPrioridadAlta(dto.getPrioridadAlta());
        ingrediente.setProveedor(dto.getProveedor());
        ingrediente.setFotoUrl(dto.getFotoUrl());

        // Buscar el restaurante por ID
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        ingrediente.setRestaurante(restaurante);

        return toDTO(ingredienteRepository.save(ingrediente));
    }
    
    public boolean eliminarIngrediente(Long id) {
        if (ingredienteRepository.existsById(id)) {
            ingredienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Mappers
    public IngredienteDTO toDTO(Ingrediente ingrediente) {
        return new IngredienteDTO(
            ingrediente.getId(),
            ingrediente.getNombre(),
            ingrediente.getUnidadMedida(),
            ingrediente.getCantidadStock(),
            ingrediente.getPrioridadBaja(),
            ingrediente.getPrioridadMedia(),
            ingrediente.getPrioridadAlta(),
            ingrediente.getProveedor(),
            ingrediente.getFotoUrl()
        );
    }



    private Ingrediente toEntity(IngredienteDTO dto) {
        Ingrediente i = new Ingrediente();
        i.setId(dto.getId());
        i.setNombre(dto.getNombre());
        i.setUnidadMedida(dto.getUnidadMedida());
        i.setCantidadStock(dto.getCantidadStock());
        i.setPrioridadBaja(dto.getPrioridadBaja());
        i.setPrioridadMedia(dto.getPrioridadMedia());
        i.setPrioridadAlta(dto.getPrioridadAlta());
        i.setProveedor(dto.getProveedor());
        i.setFotoUrl(dto.getFotoUrl());
        return i;
    }
}
