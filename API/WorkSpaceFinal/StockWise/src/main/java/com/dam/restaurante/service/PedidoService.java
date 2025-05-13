package com.dam.restaurante.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dam.restaurante.model.Cliente;
import com.dam.restaurante.model.Ingrediente;
import com.dam.restaurante.model.Pedido;
import com.dam.restaurante.model.PedidoDetalle;
import com.dam.restaurante.model.Plato;
import com.dam.restaurante.model.PlatoIngrediente;
import com.dam.restaurante.repository.ClienteRepository;
import com.dam.restaurante.repository.IngredienteRepository;
import com.dam.restaurante.repository.PedidoDetalleRepository;
import com.dam.restaurante.repository.PedidoRepository;
import com.dam.restaurante.repository.PlatoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Crear un nuevo pedido
    public Pedido crearPedido(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        return pedidoRepository.save(nuevoPedido);
    }

    // Añadir platos al carrito
    public PedidoDetalle añadirPlatoAlCarrito(Long pedidoId, Long platoId, Integer cantidad) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Plato plato = platoRepository.findById(platoId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setPedido(pedido);
        pedidoDetalle.setPlato(plato);
        pedidoDetalle.setCantidad(cantidad);
        pedidoDetalle.setPrecio(plato.getPrecio() * cantidad);

        return pedidoDetalleRepository.save(pedidoDetalle);
    }

    // Confirmar pedido: descontamos los ingredientes
    public void confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Iteramos sobre los detalles del pedido para descontar ingredientes
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findAllByPedido(pedido);

        for (PedidoDetalle detalle : detalles) {
            Plato plato = detalle.getPlato();
            List<PlatoIngrediente> platoIngredientes = plato.getIngredientes();

            for (PlatoIngrediente platoIngrediente : platoIngredientes) {
                Ingrediente ingrediente = platoIngrediente.getIngrediente();
                Double cantidadUsada = platoIngrediente.getCantidadNecesaria() * detalle.getCantidad();
                
                if (ingrediente.getCantidadStock() >= cantidadUsada) {
                    ingrediente.setCantidadStock(ingrediente.getCantidadStock() - cantidadUsada);
                    ingredienteRepository.save(ingrediente);
                } else {
                    throw new RuntimeException("No hay suficiente stock de " + ingrediente.getNombre());
                }
            }
        }

        // Confirmamos el pedido (se podría agregar estado al pedido)
        // pedido.setEstado("Confirmado");
        pedidoRepository.save(pedido);
    }
}