package com.dam.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Pedido;
import com.dam.restaurante.model.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

	List<PedidoDetalle> findAllByPedido(Pedido pedido);
    // Aquí puedes añadir consultas personalizadas si es necesario
}