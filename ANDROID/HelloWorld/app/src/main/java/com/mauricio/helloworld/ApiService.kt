import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.Ingrediente
import com.mauricio.helloworld.IngredienteRequest
import com.mauricio.helloworld.IngredienteResponse
import com.mauricio.helloworld.LoginRequest
import com.mauricio.helloworld.LoginResponse
import com.mauricio.helloworld.PlatoDTO
import com.mauricio.helloworld.Restaurante
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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


}
