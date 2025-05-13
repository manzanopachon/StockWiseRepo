package com.dam.restaurante.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dam.restaurante.model.Empleado;
import com.dam.restaurante.repository.EmpleadoRepository;

import jakarta.transaction.Transactional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Método para registrar un nuevo empleado
    @Transactional
    public Empleado registrarEmpleado(Empleado empleado) {
        // Encriptamos la contraseña
        empleado.setContraseña(passwordEncoder.encode(empleado.getContraseña()));
        
        // Generamos un código de verificación aleatorio de 5 caracteres
        String codigoVerificacion = generarCodigoVerificacion();
        empleado.setCodigoValidacion(codigoVerificacion);
        
        
        // Guardamos el empleado
        return empleadoRepository.save(empleado);
    }
    
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    // Método para generar un código de verificación aleatorio de 5 caracteres
    private String generarCodigoVerificacion() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }

    // Método para verificar el código de verificación
    public Optional<Empleado> verificarCodigo(Long id, String codigo) {
        Empleado empleado = empleadoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (empleado.getValidado()) {
            return Optional.of(empleado);
        }

        if (empleado.getCodigoValidacion().equals(codigo)) {
            empleado.setValidado(true);
            empleado.setIntentosRestantes(3); // reset
            return Optional.of(empleadoRepository.save(empleado));
        } else {
            int intentos = empleado.getIntentosRestantes() - 1;
            empleado.setIntentosRestantes(intentos);

            if (intentos <= 0) {
                empleado.setCodigoValidacion(generarCodigoVerificacion());
                empleado.setIntentosRestantes(3);
            }

            empleadoRepository.save(empleado);
            return Optional.empty();
        }
    }
    //Metodo para obtener un empleado por su ID
    public Optional<Empleado> obtenerPorId(Long id) {
        return empleadoRepository.findById(id);
    }

    // Método para iniciar sesión
    public Empleado iniciarSesion(String correo, String contraseña) {
        Empleado empleado = empleadoRepository.findByCorreo(correo);
        if (empleado != null && passwordEncoder.matches(contraseña, empleado.getContraseña())) {
            return empleado;
        }
        throw new RuntimeException("Credenciales incorrectas");
    }
}