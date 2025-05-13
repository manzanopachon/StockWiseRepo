package com.dam.restaurante.service;



import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dam.restaurante.dto.CategoriaDTO;
import com.dam.restaurante.dto.PlatoDTO;
import com.dam.restaurante.dto.PlatoDTO.IngredienteCantidadDTO;
import com.dam.restaurante.dto.PlatoIngredienteDTO;
import com.dam.restaurante.model.Categoria;
import com.dam.restaurante.model.Ingrediente;
import com.dam.restaurante.model.Plato;
import com.dam.restaurante.model.PlatoIngrediente;
import com.dam.restaurante.model.Restaurante;
import com.dam.restaurante.repository.CategoriaRepository;
import com.dam.restaurante.repository.IngredienteRepository;
import com.dam.restaurante.repository.PlatoIngredienteRepository;
import com.dam.restaurante.repository.PlatoRepository;
import com.dam.restaurante.repository.RestauranteRepository;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private PlatoIngredienteRepository platoIngredienteRepository;
    
    @Autowired
    private RestauranteRepository restauranteRepository;

    public List<PlatoDTO> obtenerTodos() {
        return platoRepository.findAll().stream().map(this::convertirAPlatoDTO).collect(Collectors.toList());
    }

    public List<PlatoDTO> obtenerTodoPlatos() {
        return platoRepository.findAll().stream()
                .map(this::convertirAPlatoDTO)
                .collect(Collectors.toList());
    }
    
    public List<PlatoDTO> obtenerPorRestauranteId(Long restauranteId) {
        
        return platoRepository.findAll().stream()
                .filter(p -> p.getRestaurante().getId().equals(restauranteId))
                .map(this::convertirAPlatoDTO)
                .collect(Collectors.toList());
    }
    
    
    public PlatoDTO obtenerPorId(Long id) {
        return platoRepository.findById(id)
                .map(this::convertirAPlatoDTO)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));
    }

    public Plato crearPlato(PlatoDTO platoDTO) {
        Plato plato = new Plato();
        plato.setNombre(platoDTO.getNombre());
        plato.setDescripcion(platoDTO.getDescripcion());
        plato.setPrecio(platoDTO.getPrecio());
        Categoria categoria = categoriaRepository.findByNombre(platoDTO.getCategoria().getNombre())
        	    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        plato.setCategoria(categoria);

        plato.setRestaurante(restauranteRepository.findById(platoDTO.getRestauranteId()).orElseThrow(() -> new RuntimeException("Restaurante no encontrado")));

        // Guardamos el plato
        Plato platoGuardado = platoRepository.save(plato);

        // Si existen ingredientes, los asociamos al plato
        if (platoDTO.getIngredientes() != null && !platoDTO.getIngredientes().isEmpty()) {
            for (PlatoDTO.IngredienteCantidadDTO ingredienteDTO : platoDTO.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepository.findById(ingredienteDTO.getIngredienteId())
                    .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

                PlatoIngrediente platoIngrediente = new PlatoIngrediente();
                platoIngrediente.setIngrediente(ingrediente);
                platoIngrediente.setPlato(platoGuardado);
                platoIngrediente.setCantidadNecesaria(ingredienteDTO.getCantidad());


                platoIngredienteRepository.save(platoIngrediente);
            }
        }
        
        return platoGuardado;
    }



    public PlatoDTO actualizarPlato(Long id, PlatoDTO dto) {
        Plato existente = platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());

        String nombreCategoria = dto.getCategoria().getNombre(); // <-- CAMBIO
        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombreCategoria));
        existente.setCategoria(categoria);

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        existente.setRestaurante(restaurante);

        return convertirAPlatoDTO(platoRepository.save(existente));
    }

    public void eliminarPlato(Long id) {
        platoRepository.deleteById(id);
    }

    public PlatoDTO convertirAPlatoDTO(Plato plato) {
        CategoriaDTO categoriaDTO = new CategoriaDTO(
            plato.getCategoria().getId(),
            plato.getCategoria().getNombre()
        );

        List<PlatoDTO.IngredienteCantidadDTO> ingredientesDTO = plato.getIngredientes().stream()
            .map(pi -> new PlatoDTO.IngredienteCantidadDTO(
                pi.getIngrediente().getId(),
                pi.getCantidadNecesaria()
            ))
            .collect(Collectors.toList());

        return new PlatoDTO(
            plato.getId(),
            plato.getNombre(),
            plato.getDescripcion(),
            plato.getPrecio(),
            categoriaDTO,
            plato.getRestaurante().getId(),
            ingredientesDTO
        );
    }



    
    public void asignarIngredientesAPlato(Long platoId, Map<Long, Double> ingredientesConCantidad) {
        Plato plato = platoRepository.findById(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + platoId));

        // Elimina relaciones anteriores si las hubiera
        platoIngredienteRepository.deleteByPlatoId(platoId);

        // Recorremos el mapa de ingredientes y cantidad
        for (Map.Entry<Long, Double> entry : ingredientesConCantidad.entrySet()) {
            Long ingredienteId = entry.getKey();
            Double cantidad = entry.getValue();

            Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                    .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + ingredienteId));

            PlatoIngrediente relacion = new PlatoIngrediente();
            relacion.setPlato(plato);
            relacion.setIngrediente(ingrediente);
            relacion.setCantidadNecesaria(cantidad);

            platoIngredienteRepository.save(relacion);
        }
    }
    
 // Modificar cantidad
    public void modificarCantidadIngrediente(Long platoId, Long ingredienteId, Double nuevaCantidad) {
        PlatoIngrediente relacion = platoIngredienteRepository.findByPlatoIdAndIngredienteId(platoId, ingredienteId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));
        relacion.setCantidadNecesaria(nuevaCantidad);
        platoIngredienteRepository.save(relacion);
    }

    public void eliminarIngredienteDePlato(Long platoId, Long ingredienteId) {
        // Obtener el plato
        Plato plato = platoRepository.findById(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + platoId));

        // Obtener el ingrediente
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + ingredienteId));

        // Eliminar la relación entre plato e ingrediente
        platoIngredienteRepository.deleteByPlatoAndIngrediente(plato, ingrediente);
    }

    // Obtener ingredientes de un plato
    public List<PlatoIngrediente> obtenerIngredientesDePlato(Long platoId) {
        return platoIngredienteRepository.findByPlatoId(platoId);
    }
    
    public List<PlatoIngredienteDTO> obtenerIngredientesDePlatoDTO(Long platoId) {
        List<PlatoIngrediente> relaciones = platoIngredienteRepository.findByPlatoId(platoId);

        return relaciones.stream().map(relacion -> {
            Ingrediente ingrediente = relacion.getIngrediente();
            return new PlatoIngredienteDTO(
                    ingrediente.getId(),
                    ingrediente.getNombre(),
                    ingrediente.getUnidadMedida(),
                    ingrediente.getCantidadStock(),
                    relacion.getCantidadNecesaria()
                    
            );
        }).collect(Collectors.toList());
    }
    
 // Obtener todos los platos con sus ingredientes
    public List<PlatoDTO> obtenerTodosConIngredientes() {
        return platoRepository.findAll().stream()
                .map(plato -> {
                    // Obtener los ingredientes del plato como PlatoIngredienteDTO
                    List<PlatoIngredienteDTO> ingredientesPlato = obtenerIngredientesDePlatoDTO(plato.getId());
                    
                    // Convertir cada PlatoIngredienteDTO a IngredienteCantidadDTO
                    List<IngredienteCantidadDTO> ingredientes = ingredientesPlato.stream()
                            .map(pi -> new IngredienteCantidadDTO(pi.getIngredienteId(), pi.getCantidadNecesaria()))
                            .collect(Collectors.toList());
                    
                    // Convertir el plato a PlatoDTO
                    PlatoDTO dto = convertirAPlatoDTO(plato);
                    
                    // Asignar la lista de IngredienteCantidadDTO a PlatoDTO
                    dto.setIngredientes(ingredientes);
                    return dto;
                })
                .collect(Collectors.toList());
    }
 // Obtener platos por restaurante con ingredientes incluidos
    public List<PlatoDTO> obtenerPorRestauranteConIngredientes(Long restauranteId) {
        return platoRepository.findAll().stream()
                .filter(p -> p.getRestaurante().getId().equals(restauranteId))
                .map(plato -> {
                    // Obtener los ingredientes del plato como PlatoIngredienteDTO
                    List<PlatoIngredienteDTO> ingredientesPlato = obtenerIngredientesDePlatoDTO(plato.getId());
                    
                    // Convertir cada PlatoIngredienteDTO a IngredienteCantidadDTO
                    List<IngredienteCantidadDTO> ingredientes = ingredientesPlato.stream()
                            .map(pi -> new IngredienteCantidadDTO(pi.getIngredienteId(), pi.getCantidadNecesaria()))
                            .collect(Collectors.toList());
                    
                    // Convertir el plato a PlatoDTO
                    PlatoDTO dto = convertirAPlatoDTO(plato);
                    
                    // Asignar la lista de IngredienteCantidadDTO a PlatoDTO
                    dto.setIngredientes(ingredientes);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    // Reasignar todos los ingredientes de un plato (sobrescribe los actuales)
    public void reasignarIngredientesPlato(Long platoId, List<PlatoIngredienteDTO> nuevosIngredientes) {
        Plato plato = platoRepository.findById(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + platoId));

        // Eliminar relaciones existentes
        platoIngredienteRepository.deleteByPlatoId(platoId);

        // Crear nuevas relaciones
        for (PlatoIngredienteDTO dto : nuevosIngredientes) {
            Ingrediente ingrediente = ingredienteRepository.findById(dto.getIngredienteId())
                    .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado con ID: " + dto.getIngredienteId()));

            PlatoIngrediente relacion = new PlatoIngrediente();
            relacion.setPlato(plato);
            relacion.setIngrediente(ingrediente);
            relacion.setCantidadNecesaria(dto.getCantidadNecesaria());

            platoIngredienteRepository.save(relacion);
        }
    }



}
