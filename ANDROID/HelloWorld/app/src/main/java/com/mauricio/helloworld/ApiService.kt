import com.mauricio.helloworld.CategoriaDTO
import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.Ingrediente
import com.mauricio.helloworld.IngredienteRequest
import com.mauricio.helloworld.IngredienteResponse
import com.mauricio.helloworld.LoginRequest
import com.mauricio.helloworld.LoginResponse
import com.mauricio.helloworld.Pedido
import com.mauricio.helloworld.PlatoDTO
import com.mauricio.helloworld.Restaurante
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

}
