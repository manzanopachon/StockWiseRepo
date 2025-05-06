import com.mauricio.helloworld.Empleado
import com.mauricio.helloworld.Ingrediente
import com.mauricio.helloworld.LoginRequest
import com.mauricio.helloworld.LoginResponse
import com.mauricio.helloworld.Restaurante
import retrofit2.Call
import retrofit2.http.Body
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

    @GET("restaurantes/nombre/{nombre}")
    fun getRestaurantePorNombre(@Path("nombre") nombre: String): Call<Restaurante>

    //LOGIN
    @POST("api/auth/login")
    fun loginEmpleado(@Body request: LoginRequest): Call<LoginResponse>

    //VERIFICAR
    @POST("api/auth/verificar/{id}")
    fun verificarCodigo(
        @Path("id") id: Long,
        @Query("codigo") codigo: String
    ): Call<Empleado>

    @GET("api/empleados/{id}")
    fun obtenerEmpleadoPorId(@Path("id") id: Long): Call<Empleado>

    //VALIDAMOS EMPLEADO
    @PUT("api/empleados/validar/{id}")
    fun marcarEmpleadoComoValidado(@Path("id") id: Long): Call<Void>

    //GENERAR CODIGO NUEVO
    @POST("api/empleados/regenerar-codigo/{id}")
    fun regenerarCodigo(@Path("id") id: Long): Call<Void>

    //PANTALLA DEL EMPLEADO
    @GET("ingredientes/restaurante/{restauranteId}")
    suspend fun getIngredientesByRestaurante(
        @Path("restauranteId") restauranteId: Long
    ): List<Ingrediente>


}
