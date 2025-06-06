package com.mauricio.stockwise.service

import com.mauricio.stockwise.model.CategoriaDTO
import com.mauricio.stockwise.model.Empleado
import com.mauricio.stockwise.model.Ingrediente
import com.mauricio.stockwise.model.IngredienteRequest
import com.mauricio.stockwise.model.IngredienteResponse
import com.mauricio.stockwise.model.LlamadaCamarero
import com.mauricio.stockwise.model.LoginRequest
import com.mauricio.stockwise.model.LoginResponse
import com.mauricio.stockwise.model.Pedido
import com.mauricio.stockwise.model.PlatoDTO
import com.mauricio.stockwise.model.Restaurante
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Endpoint para registrar un empleado
    @POST("/api/auth/registro")
    fun registrarEmpleado(@Body empleado: Empleado): Call<Empleado>

    @GET("restaurantes/nombre/{nombre}")
    fun buscarRestaurantePorNombre(@Path("nombre") nombre: String): Call<Restaurante>

    @GET("api/restaurantes")
    fun obtenerTodosLosRestaurantes(): Call<List<Restaurante>>


    //LOGIN
    @POST("api/auth/login")
    fun loginEmpleado(@Body request: LoginRequest): Call<LoginResponse>

    //VERIFICAR
    @POST("api/auth/verificar/{id}")
    fun verificarCodigo(
        @Path("id") id: Long,
        @Query("codigo") codigo: String
    ): Call<Empleado>

    @GET("api/auth/{id}")
    suspend fun obtenerEmpleadoPorId(@Path("id") id: Long): Empleado


    //VALIDAMOS EMPLEADO
    @PUT("api/empleados/validar/{id}")
    fun marcarEmpleadoComoValidado(@Path("id") id: Long): Call<Void>

    //GENERAR CODIGO NUEVO
    @POST("api/empleados/regenerar-codigo/{id}")
    fun regenerarCodigo(@Path("id") id: Long): Call<Void>

    //PANTALLA DEL EMPLEADO
    @GET("api/ingredientes/restaurante/{restauranteId}")
    suspend fun getIngredientesByRestaurante(
        @Path("restauranteId") restauranteId: Long
    ): List<Ingrediente>


    //Ingredientes:
    @POST("api/ingredientes/crear")
    fun crearIngrediente(@Body ingrediente: IngredienteRequest): Call<IngredienteResponse>

    @GET("api/ingredientes/{id}")
    suspend fun getIngredienteById(@Path("id") id: Long): Ingrediente

    @PUT("api/ingredientes/{id}")
    suspend fun actualizarIngrediente(@Path("id") id: Long, @Body ingrediente: Ingrediente): Ingrediente

    @DELETE("api/ingredientes/{id}")
    suspend fun eliminarIngrediente(@Path("id") id: Long): Void


    //PLATOS

    @GET("api/platos/restaurante/{restauranteId}")
    suspend fun obtenerPlatosPorRestaurante(@Path("restauranteId") restauranteId: Long): List<PlatoDTO>


    @POST("api/platos")
    suspend fun crearPlato(@Body platoDTO: PlatoDTO): PlatoDTO

    /*
    @GET("api/ingredientes/ingredientes/{restauranteId}")
    suspend fun obtenerIngredientesPorRestaurante(@Path("restauranteId") restauranteId: Long): List<Ingrediente>
    */

    // Eliminar plato
    @DELETE("/api/platos/{id}")
    suspend fun eliminarPlato(@Path("id") platoId: Long)

    // Eliminar ingrediente de un plato
    @DELETE("/api/platos/{platoId}/ingredientes/{ingredienteId}")
    suspend fun eliminarIngredienteDePlato(
        @Path("platoId") platoId: Long,
        @Path("ingredienteId") ingredienteId: Long
    )

    @GET("api/categorias")
    suspend fun getCategorias(): List<CategoriaDTO>

    //EDITAR PLATOS

    //Obtener plato por ID
    @GET("api/platos/{id}")
    suspend fun obtenerPlatoPorId(@Path("id") platoId: Long): PlatoDTO

    //Actualizar un plato
    @PUT("api/platos/{id}")
    suspend fun actualizarPlato(@Path("id") platoId: Long, @Body platoDTO: PlatoDTO): PlatoDTO

    //Modificar cantidad de un ingrediente en un plato
    @PUT("api/platos/{platoId}/ingredientes/{ingredienteId}")
    suspend fun modificarCantidadIngrediente(
        @Path("platoId") platoId: Long,
        @Path("ingredienteId") ingredienteId: Long,
        @Body nuevaCantidad: Double
    ): ResponseBody

    //Obtener ingredientes asignados a un plato
    @GET("api/platos/{platoId}/ingredientes")
    suspend fun obtenerIngredientesDePlato(@Path("platoId") platoId: Long): List<Ingrediente>

    //Añadir ingredientes que no están en el plato
    @POST("api/platos/{platoId}/ingredientes")
    suspend fun asignarIngredientesAlPlato(
        @Path("platoId") platoId: Long,
        @Body ingredientesConCantidad: Map<Long, Double>
    ): ResponseBody


    //PEDIDOS

    @GET("api/pedidos/restaurante/{id}/pendientes")
    suspend fun obtenerPedidosPendientes(@Path("id") restauranteId: Long): List<Pedido>

    @POST("api/pedidos/confirmar/{id}")
    suspend fun confirmarPedido(@Path("id") id: Long): ResponseBody

    @PATCH("api/pedidos/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Query("estado") estado: String
    ): ResponseBody

    @GET("api/pedidos/buscar/{codigo}")
    suspend fun buscarPedidoPorCodigo(@Path("codigo") codigo: String): Pedido

    @GET("api/pedidos/restaurante/{id}/todos")
    suspend fun obtenerTodosLosPedidos(@Path("id") restauranteId: Long): List<Pedido>


    //NOTIFICACION

    @GET("api/llamadas/pendientes")
    suspend fun obtenerLlamadasPendientes(): List<LlamadaCamarero>

}
