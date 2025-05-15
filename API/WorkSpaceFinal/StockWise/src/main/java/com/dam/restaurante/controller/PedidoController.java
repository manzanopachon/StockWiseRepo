package com.dam.restaurante.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.restaurante.dto.PedidoDTO;
import com.dam.restaurante.model.Ingrediente;
import com.dam.restaurante.model.Pedido;
import com.dam.restaurante.model.Plato;
import com.dam.restaurante.model.PlatoIngrediente;
import com.dam.restaurante.model.Restaurante;
import com.dam.restaurante.repository.IngredienteRepository;
import com.dam.restaurante.repository.PedidoRepository;
import com.dam.restaurante.repository.PlatoRepository;
import com.dam.restaurante.repository.RestauranteRepository;

import jakarta.transaction.Transactional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
	
	 @Autowired
	 private RestauranteRepository restauranteRepository;

	 @Autowired
	 private PlatoRepository platoRepository;

	 @Autowired
	 private IngredienteRepository ingredienteRepository;

	 @Autowired
	 private PedidoRepository pedidoRepository;

	 
	 @PostMapping("/crear")
	 @Transactional
	 public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO dto) {
		// 1. Verificar restaurante
		 Optional<Restaurante> restauranteOpt = restauranteRepository.findById(dto.getRestauranteId());
		if (restauranteOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Restaurante no encontrado");
		}
		Restaurante restaurante = restauranteOpt.get();
	 
		// 2. Verificar platos
		List<Long> idsPlatos = dto.getPlatos();
		List<Plato> platos = platoRepository.findAllById(idsPlatos);

		if (platos.isEmpty()) {
			return ResponseEntity.badRequest().body("No se encontró ningún plato válido");
		}
		 // 3. Crear el pedido desde el DTO
		 Pedido pedido = Pedido.fromDTO(dto, restaurante, platos);
	 
		 // 4. Calcular ingredientes a restar
		 Map<Ingrediente, Double> ingredientesARestar = new HashMap<>();
		 for (Plato plato : platos) {
			 for (PlatoIngrediente pi : plato.getIngredientes()) {
				 ingredientesARestar.merge(
					 pi.getIngrediente(),
					 pi.getCantidadNecesaria(),
					 Double::sum
				 );
			 }
		 }
	 
		 // 5. Validar y actualizar stock
		 for (Map.Entry<Ingrediente, Double> entry : ingredientesARestar.entrySet()) {
			 Ingrediente ingrediente = entry.getKey();
			 double cantidadRestar = entry.getValue();
	 
			 if (ingrediente.getCantidadStock() < cantidadRestar) {
				 return ResponseEntity.badRequest()
					 .body("No hay suficiente stock para " + ingrediente.getNombre());
			 }
	 
			 ingrediente.setCantidadStock(ingrediente.getCantidadStock() - cantidadRestar);
			 ingredienteRepository.save(ingrediente);
		 }
	 
		 // 6. Guardar el pedido
		 pedidoRepository.save(pedido);
	 
		 // 7. Responder
		 return ResponseEntity.ok("Pedido realizado correctamente");
	 }
	 

}
