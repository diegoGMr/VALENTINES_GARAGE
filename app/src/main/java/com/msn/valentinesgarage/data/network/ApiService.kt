package com.msn.valentinesgarage.data.network

import com.msn.valentinesgarage.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth / User ───────────────────────────────────
    @POST("user/loginUser")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("user/registerUser")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("user/getUserWithId/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<User>

    @PUT("user/updateUser/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: RegisterRequest,
    ): Response<Map<String, Int>>

    // ── Client ────────────────────────────────────────
    @POST("client/register")
    suspend fun registerClient(
        @Header("Authorization") token: String,
        @Body request: RegisterClientRequest,
    ): Response<RegisterClientResponse>

    @GET("client/getClientById/{clientId}")
    suspend fun getClientById(
        @Header("Authorization") token: String,
        @Path("clientId") clientId: Int,
    ): Response<Client>

    @GET("client/getClientByHex/{hexId}")
    suspend fun getClientByHex(
        @Header("Authorization") token: String,
        @Path("hexId") hexId: String,
    ): Response<Client>

    @POST("client/newVisit")
    suspend fun createClientVisit(
        @Header("Authorization") token: String,
        @Body request: NewVisitRequest,
    ): Response<NewVisitResponse>

    // ── Truck ─────────────────────────────────────────
    @POST("truck/register")
    suspend fun registerTruck(
        @Header("Authorization") token: String,
        @Body request: RegisterTruckRequest,
    ): Response<RegisterTruckResponse>

    @GET("truck/getTruck/{id}")
    suspend fun getTruckById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<Truck>
}
