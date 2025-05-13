package com.dam.restaurante.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.restaurante.dto.EmpleadoDTO;
import com.dam.restaurante.dto.LoginRequest;
import com.dam.restaurante.model.Empleado;
import com.dam.restaurante.service.EmpleadoService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public List<Empleado> obtenerTodos() {
        return empleadoService.obtenerTodos();
    }
    //Endpoint para ver un empleado
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEmpleadoPorId(@PathVariable Long id) {
        Optional<Empleado> empleado = empleadoService.obtenerPorId(id);
        if (empleado.isPresent()) {
            return ResponseEntity.ok(new EmpleadoDTO(empleado.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
        }
    }

    
    // Endpoint para registrar un empleado
    @PostMapping("/registro")
    public EmpleadoDTO registrarEmpleado(@RequestBody Empleado empleado) {
        Empleado nuevoEmpleado = empleadoService.registrarEmpleado(empleado);
        return new EmpleadoDTO(nuevoEmpleado);
    }

    // Endpoint para verificar el código de verificación
    @PostMapping("/verificar/{id}")
    public ResponseEntity<?> verificarCodigo(
        @PathVariable Long id,
        @RequestParam String codigo
    ) {
        Optional<Empleado> optionalEmpleado = empleadoService.verificarCodigo(id, codigo);

        if (optionalEmpleado.isPresent()) {
            return ResponseEntity.ok(new EmpleadoDTO(optionalEmpleado.get()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Código incorrecto");
        }
    }


 // Endpoint para iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Empleado empleado = empleadoService.iniciarSesion(
                    loginRequest.getCorreo(),
                    loginRequest.getContraseña());

            if (!empleado.getValidado()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Primera vez. Introduce el código de verificación",
                        "requiereCodigo", true,
                        "empleadoId", empleado.getId()
                ));
            }

            // Asegúrate de que el nombre esté en la respuesta
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Login exitoso",
                    "requiereCodigo", false,
                    "empleadoId", empleado.getId(),
                    "nombreEmpleado", empleado.getNombre()  // Asegúrate de incluir el nombre aquí
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    

}