# StockWise — Gestión de inventario y pedidos para restaurantes

**Proyecto individual de final de ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM)**  
**Autor:** Juan Mauricio Manzano Pachón

StockWise es una solución para restaurantes que integra una aplicación Android, una API REST y una base de datos. Permite gestionar ingredientes, productos, platos y pedidos, y mantener actualizado el inventario mediante el descuento automático de ingredientes utilizados en cada pedido.

También incluye una carta digital accesible mediante código QR, desde la que los clientes pueden realizar pedidos y consultar su estado.

## Tecnologías utilizadas

| Componente | Tecnologías |
| --- | --- |
| Backend | Java 17, Spring Boot, Spring Web MVC |
| Seguridad | Spring Security, JWT |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | MySQL |
| Aplicación móvil | Kotlin, Android |
| Web | HTML y tecnologías web del proyecto |
| Compilación y despliegue del backend | Maven, Docker |

## Funcionalidades principales

- Alta y gestión de productos, ingredientes y platos.
- Registro y gestión de pedidos.
- Validación de disponibilidad de ingredientes antes de confirmar pedidos.
- Descuento automático del stock asociado a los platos de cada pedido.
- Avisos de existencias bajas.
- Seguimiento de pedidos mediante un código identificativo y estados como `PENDIENTE`, `EN_PROCESO` y `FINALIZADO`.
- Carta digital accesible mediante código QR.
- Aplicación Android para la gestión del restaurante y página web orientada al cliente.

## Arquitectura general

```text
Aplicación Android (Kotlin) ──────┐
                                  ├── API REST (Java / Spring Boot) ── MySQL
Carta digital / web del cliente ──┘
```

El backend se organiza por capas: controladores REST, lógica de negocio, entidades y repositorios de persistencia. La API se encarga de procesar pedidos, aplicar las reglas del inventario y conectar los clientes con los datos del restaurante.

## Capturas de pantalla

> **Pendiente:** añadir capturas reales de la pantalla principal, inventario, creación de platos, gestión de pedidos y carta digital. Guardarlas en una carpeta `docs/img/` y enlazarlas aquí.

<!-- Ejemplo de formato al añadir una imagen:
![Gestión de inventario](docs/img/inventario.png)
-->

## Estructura del repositorio

```text
StockWiseRepo/
├── ANDROID/StockWise/       # Aplicación Android
├── API/WorkSpaceFinal/     # Backend y API REST
├── DOCUMENTACION/          # Documentación del proyecto
├── CartaRes.html           # Carta web
└── README.md               # Presentación del proyecto
```

## Ejecución local

El repositorio contiene el código de la aplicación Android y del backend. Para ejecutarlos localmente es necesario configurar un entorno de desarrollo y una base de datos MySQL.

**Requisitos generales:** JDK 17, Maven, MySQL y Android Studio para la aplicación móvil.

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/manzanopachon/StockWiseRepo.git
   ```

2. Abrir el proyecto backend en `API/WorkSpaceFinal/` y preparar la configuración local de la base de datos. Las credenciales deben almacenarse fuera del repositorio, por ejemplo mediante variables de entorno.
3. Preparar el esquema y los datos necesarios para ejecutar la API.
4. Iniciar el backend y configurar la dirección de la API utilizada por el cliente Android.
5. Abrir `ANDROID/StockWise/` con Android Studio y ejecutar la aplicación en un emulador o dispositivo compatible.

> **Nota:** estos son pasos generales, no una guía de instalación verificada de extremo a extremo. Completar los comandos exactos de arranque, esquema de base de datos, configuración Android y datos de prueba cuando se hayan comprobado.

## Código y documentación

- [Repositorio completo](https://github.com/manzanopachon/StockWiseRepo)
- [Documentación técnica de la API](https://deepwiki.com/manzanopachon/StockWiseApi/1-overview)
- [Perfil de LinkedIn](https://www.linkedin.com/in/mauriciomanzano/)

## Autor

**Juan Mauricio Manzano Pachón**  
Técnico Superior en Desarrollo de Aplicaciones Multiplataforma y estudiante de Ingeniería Informática en la Universidad de Sevilla.

---

*Este proyecto fue desarrollado individualmente como proyecto final de DAM. La documentación, las capturas y la guía de instalación se seguirán ampliando.*
