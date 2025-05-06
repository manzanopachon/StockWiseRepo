package com.dam.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Aquí puedes añadir consultas personalizadas si es necesario
}
