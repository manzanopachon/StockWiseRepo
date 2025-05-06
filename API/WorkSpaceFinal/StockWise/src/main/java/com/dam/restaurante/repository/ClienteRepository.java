package com.dam.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.restaurante.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Buscar por número de mesa
    Cliente findByNumeroMesa(Integer numeroMesa);
}